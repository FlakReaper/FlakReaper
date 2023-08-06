package io.github.flakreaper.analyzer.stackmatcher;

import io.github.flakreaper.analyzer.stackmatcher.core.ProfileData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class TraceMatcher {

    private List<ProfileData> shortList = new ArrayList<>();

    private static final String DISPATCH_MSG_NAME = "android.os.Handler.dispatchMessage";


    /**
     * From bottom to top, match nodes in the trace tree according to the exception Stack
     * @param exceptionStack
     * @return
     */
    public ProfileData match(List<ProfileData> nodeList, Stack<String> exceptionStack) {
        // The top method name of the exception stack
        String topFunction = exceptionStack.pop();
        // The node corresponding to the top method name
        List<ProfileData> topNodeList = nodeList.stream()
                // .filter(node -> node.getName().equals("android.support.test.espresso.ViewInteraction.runSynchronouslyOnUiThread"))
                .filter(node -> node.getName().equals(topFunction))
                .collect(Collectors.toList());
        // Calculate the similarity of the whole link corresponding to each node
        List<Integer> similarityList = calculateSimilarityList(exceptionStack, topNodeList);
        ProfileData optimalNode = getOptimalNode(topNodeList, similarityList);
        // Find the node of the current dispatchMsg
        ProfileData currentDispatchMessageNode = optimalNode;
        while (currentDispatchMessageNode != null) {
            if (DISPATCH_MSG_NAME.equals(currentDispatchMessageNode.getName())) {
                break;
            }
            currentDispatchMessageNode = currentDispatchMessageNode.getParent();
        }
        double startTime = currentDispatchMessageNode.getGlobalStartTimeInMillisecond();
        double endTime = currentDispatchMessageNode.getGlobalEndTimeInMillisecond();

        int k = Integer.MAX_VALUE;
        List<ProfileData> frontDispatchMessageNodeList = nodeList.stream()
                .filter(node -> DISPATCH_MSG_NAME.equals(node.getName()))
                .filter(node -> node.getGlobalEndTimeInMillisecond() <= startTime)
                .collect(Collectors.toList());
        filterInvalidNode(frontDispatchMessageNodeList);

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
     * Filter out invalid elements. If a node's children exist in the list, delete the current node
     * @param dispatchMessageNodeList
     */
    private void filterInvalidNode(List<ProfileData> dispatchMessageNodeList) {
        Iterator<ProfileData> iterator = dispatchMessageNodeList.iterator();
        while (iterator.hasNext()) {
            ProfileData node = iterator.next();
            // The first element does not need to be contrasted
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
                // If a node's children exist in the list, the current node is deleted
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
     * The optimal solution is obtained by comparing the similarity
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

            int max = similarityList.stream().mapToInt(Integer::intValue).max().orElse(0);
            List<Integer> similarityMaxPosList = new ArrayList<>();
            for (int i = 0; i < similarityList.size(); i++) {
                if (similarityList.get(i) == max) {
                    similarityMaxPosList.add(i);
                }
            }
            if (similarityMaxPosList.size() == 1) {
                return topNodeList.get(similarityMaxPosList.get(0));
            } else {
                return topNodeList.get(1);
            }
        }
    }

    /**
     * Calculate similarity: the number of successful comparisons with trace links in the method stack
     * @param exceptionStack
     * @param topNodeList
     * @return
     */
    @NotNull
    private List<Integer> calculateSimilarityList(Stack<String> exceptionStack, List<ProfileData> topNodeList) {
        String currentTopcuFunction = null;
        List<Integer> similarityList = new ArrayList<>(topNodeList.size());
        for (ProfileData topNode: topNodeList) {
            Stack<String> clonedStack = new Stack<>();
            clonedStack.addAll(exceptionStack);
            ProfileData node = topNode.getParent();
            int similarity = 1;
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
                    isPop = false;
                }
                node = node.getParent();
            }
            similarityList.add(similarity);
        }
        return similarityList;
    }
}
