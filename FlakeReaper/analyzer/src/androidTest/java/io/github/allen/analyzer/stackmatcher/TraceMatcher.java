package io.github.allen.analyzer.stackmatcher;

import io.github.allen.analyzer.stackmatcher.core.ProfileData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class TraceMatcher {

    private List<ProfileData> shortList = new ArrayList<>();

    private static final String DISPATCH_MSG_NAME = "android.os.Handler.dispatchMessage";


    /**
     * 自下而上，根据异常Stack匹配trace树中的node
     * @param exceptionStack
     * @return
     */
    public ProfileData match(List<ProfileData> nodeList, Stack<String> exceptionStack) {
        // 异常堆栈的顶部方法名
        String topFunction = exceptionStack.pop();
        // 顶部方法名对应的node
        List<ProfileData> topNodeList = nodeList.stream()
                // .filter(node -> node.getName().equals("android.support.test.espresso.ViewInteraction.runSynchronouslyOnUiThread"))
                .filter(node -> node.getName().equals(topFunction))
                .collect(Collectors.toList());
        // 计算每个结点对应的整条链路的相似度
        List<Integer> similarityList = calculateSimilarityList(exceptionStack, topNodeList);
        // 对比相似度取最优解
        ProfileData optimalNode = getOptimalNode(topNodeList, similarityList);
        // 找前后k个(trace偏下方的，粒度更高)android.os.Handler.dispatchMessage
        // 找到当前dispatchMsg的node
        ProfileData currentDispatchMessageNode = optimalNode;
        while (currentDispatchMessageNode != null) {
            if (DISPATCH_MSG_NAME.equals(currentDispatchMessageNode.getName())) {
                break;
            }
            currentDispatchMessageNode = currentDispatchMessageNode.getParent();
        }
        double startTime = currentDispatchMessageNode.getGlobalStartTimeInMillisecond();
        double endTime = currentDispatchMessageNode.getGlobalEndTimeInMillisecond();
        // 筛选前k个dispatchMessage结点和后k个dispatchMessage结点
        int k = Integer.MAX_VALUE;
        List<ProfileData> frontDispatchMessageNodeList = nodeList.stream()
                .filter(node -> DISPATCH_MSG_NAME.equals(node.getName()))
                .filter(node -> node.getGlobalEndTimeInMillisecond() <= startTime)
                .collect(Collectors.toList());
        filterInvalidNode(frontDispatchMessageNodeList);
        // 截取前k个
        frontDispatchMessageNodeList = frontDispatchMessageNodeList.stream().limit(k).collect(Collectors.toList());

        List<ProfileData> rearDispatchMessageNodeList = nodeList.stream()
                .filter(node -> DISPATCH_MSG_NAME.equals(node.getName()))
                .filter(node -> node.getGlobalStartTimeInMillisecond() >= endTime)
                .collect(Collectors.toList());
        filterInvalidNode(rearDispatchMessageNodeList);
        rearDispatchMessageNodeList = rearDispatchMessageNodeList.stream().limit(k).collect(Collectors.toList());

        return null;
    }

    /**
     * 过滤无效元素，如果node的子孙结点存在list中，那么就删除当前结点
     * @param dispatchMessageNodeList
     */
    private void filterInvalidNode(List<ProfileData> dispatchMessageNodeList) {
        Iterator<ProfileData> iterator = dispatchMessageNodeList.iterator();
        while (iterator.hasNext()) {
            ProfileData node = iterator.next();
            // 第一个元素不需要做对比
            boolean isFirstELem = true;
            // bfs
            Queue<ProfileData> queue = new LinkedList<>();
            queue.add(node);
            while (!queue.isEmpty()) {
                ProfileData currentNode = queue.remove();
                if (isFirstELem) {
                    isFirstELem = false;
                    currentNode.getChildren().forEach(child -> queue.add(child));
                    continue;
                }
                // 如果node的子孙结点存在list中，那么就删除当前结点
                boolean isInNodeList = dispatchMessageNodeList.stream().anyMatch(p ->
                    p.getName().equals(currentNode.getName())
                            && p.getGlobalStartTimeInMillisecond() == currentNode.getGlobalStartTimeInMillisecond()
                );
                if (isInNodeList) {
                    iterator.remove();
                    break;
                }
                currentNode.getChildren().forEach(child -> queue.add(child));
            }
        }
    }

    /**
     * 对比相似度取最优解
     * @param topNodeList
     * @param similarityList
     * @return
     */
    private ProfileData getOptimalNode(List<ProfileData> topNodeList, List<Integer> similarityList) {
        if (similarityList.size() == 0) {
            throw new RuntimeException("similarityList is empty");
        } else if (similarityList.size() == 1) {
            return topNodeList.get(0);
        } else {
            // similarityList有多条，此时要找出相似度最大的trace对应的node，若最大的有多条，使用"忙长策略"

            // 找到最大值
            int max = similarityList.stream().mapToInt(Integer::intValue).max().orElse(0);
            List<Integer> similarityMaxPosList = new ArrayList<>();
            for (int i = 0; i < similarityList.size(); i++) {
                // 找到所有等于最大值的元素并放入新列表中
                if (similarityList.get(i) == max) {
                    similarityMaxPosList.add(i);
                }
            }
            if (similarityMaxPosList.size() == 1) {
                return topNodeList.get(similarityMaxPosList.get(0));
            } else {
                // TODO 使用忙长策略
                return topNodeList.get(1);
            }
        }
    }

    /**
     * 计算相似度：方法栈中与trace链路对比成功的数量
     * @param exceptionStack
     * @param topNodeList
     * @return
     */
    @NotNull
    private List<Integer> calculateSimilarityList(Stack<String> exceptionStack, List<ProfileData> topNodeList) {
        String currentTopcuFunction = null;
        List<Integer> similarityList = new ArrayList<>(topNodeList.size());
        for (ProfileData topNode: topNodeList) {
            // 重新拷贝，可以重复弹栈
            Stack<String> clonedStack = new Stack<>();
            clonedStack.addAll(exceptionStack);
            ProfileData node = topNode.getParent();
            // 栈顶元素已匹配，相似度从1开始计算
            int similarity = 1;
            // 是否弹栈，用于异常堆栈少打的情况，若方法匹配不上，则trace结点向上找父节点继续与当前方法对比
            boolean isPop = true;
            while (node != null) {
                if (clonedStack.isEmpty()) {
                    break;
                }
                if (isPop) {
                    currentTopcuFunction = clonedStack.pop();
                }
                if (node.getName().equals(currentTopcuFunction)) {
                    similarity += 1;
                    isPop = true;
                } else {
                    // 若方法匹配不上，则trace结点向上找父节点继续与当前方法对比
                    isPop = false;
                }
                node = node.getParent();
            }
            similarityList.add(similarity);
        }
        return similarityList;
    }
}
