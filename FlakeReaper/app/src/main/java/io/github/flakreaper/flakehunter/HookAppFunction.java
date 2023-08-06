package io.github.flakreaper.flakehunter;

import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;

//import androidx.test.espresso.core.internal.deps.guava.util.concurrent.ListenableFuture;
//import androidx.test.espresso.core.internal.deps.guava.util.concurrent.ListenableFutureTask;

import com.google.common.collect.Lists;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.common.util.concurrent.ListenableFuture;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookAppFunction implements IXposedHookLoadPackage {

    private final Map<String, Integer> staticIdMap = new ConcurrentHashMap<>();

    int perform_flag = 0;
    int check_flag = 0;

    int delay_flag = 0;

    private MessageQueue messageQueue;


    private long delay = 50;

    private long start_time = 0;

    int flag = 0;

    List<String> packageNameLists = Lists.newArrayList("org.gnucash.android","io.github.marktony.espresso",
            "fr.neamar.kiss.debug","com.google.android.flexbox.apps.catgallery","at.huber.youtubeExtractor.test",
            "org.mozilla.rocket.debug.iverson3","org.totschnig.myexpenses.debug","com.nononsenseapps.feeder.debug",
            "cs.ualberta.ca.medlog","de.danoeh.antennapod.debug","com.lebanmohamed.stormy","wallet.zilliqa");


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (packageNameLists.contains(loadPackageParam.packageName)) {
            XposedHelpers.findAndHookMethod(MessageQueue.class,
                    "enqueueMessage", Message.class, long.class, new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            String timeNow = FlakeHunterUtils.getTimeNow();
                            String threadinfos = "";

                            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                                threadinfos += stackTraceElement.getClassName() + stackTraceElement.getMethodName() + "\n";
                            }

                            if (!staticIdMap.containsKey(threadinfos)) {
                                staticIdMap.put(threadinfos, 0);
                            }

                            int index = staticIdMap.get(threadinfos) + 1;
                            staticIdMap.put(threadinfos, index);

                            String staticId = "" + FlakeHunterUtils.hash(threadinfos) + index;

                            FlakeHunterUtils.log(Thread.currentThread().getName() + ","
                                    + timeNow + ","
                                    + "enqueueMessage beforeHookedMethod,"
                                    + FlakeHunterUtils.hash((Message) param.args[0], (long) param.args[1]) + ","
                                    + FlakeHunterUtils.hash(threadinfos) + index);

//                            XposedBridge.log("======================before "+Thread.currentThread().getName()+" enqueueMessage==============================");
//                            Message head = (Message) FlakeHunterUtils.getProperty(param.thisObject,"mMessages");
//                            if (head!=null){ //&& message.getCallback().getClass().getName().equals("android.view.Choreographer$FrameDisplayEventReceiver")
////                                XposedBridge.log("hook android.view.Choreographer$FrameDisplayEventReceiver message");
//                                XposedBridge.log(head.toString()+head.isAsynchronous());
//                                Message next = (Message) FlakeHunterUtils.getProperty(head,"next");
//                                while (next!=null){
//                                    XposedBridge.log(next.toString()+next.isAsynchronous());
//                                    next = (Message) FlakeHunterUtils.getProperty(next,"next");
//                                }
////                                XposedBridge.log(FlakeHunterUtils.getProperty(param.thisObject,"mMessages").toString());
////                                Thread.sleep(500);
//                            }
//                            XposedBridge.log("\n");

//                            Message message = (Message) param.args[0];

                            if (FlakeHunterUtils.getStaticIdSet().contains(staticId)) {
//                                int delayDuration = 1000;
//
//                                XposedBridge.log("Delay event hit!");
//                                XposedBridge.log("Thread Name: " + Thread.currentThread().getName());
//                                XposedBridge.log("Event Static ID : " + staticId);
//                                XposedBridge.log("Event Info : " + param.args[0].toString());
//                                XposedBridge.log("Current Time: " + timeNow);
//                                XposedBridge.log("Delay Duration: " + delayDuration + "ms");
//                                Thread.sleep(delayDuration);
                            }

//                            if (delay_flag ==1 && message!=null && message.getCallback()!=null && message.getCallback().getClass().getName().equals("android.view.Choreographer$FrameDisplayEventReceiver")){
//                                XposedBridge.log("======================set Delay==============================");
//                                    Long when = (long)param.args[1]+500;
//                                    param.args[1]=when;
//                            }
//                            if (messageQueue==null){
//                                messageQueue = (MessageQueue) param.thisObject;
//                            }

                        }

                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            FlakeHunterUtils.log(Thread.currentThread().getName() + ","
                                    + FlakeHunterUtils.getTimeNow() + ","
                                    + "enqueueMessage,"
                                    + FlakeHunterUtils.hash((Message) param.args[0]) + ","
                                    + "enqueueMessage afterHookedMethod");

//                            long current_time = System.currentTimeMillis();
//                            if (delay_flag==1 && current_time-start_time < delay){
//                                XposedBridge.log("======================clear the Queue========================");
//                                @SuppressLint("SoonBlockedPrivateApi") Method privateMethod = MessageQueue.class.getDeclaredMethod("removeAllMessagesLocked");
//                                privateMethod.setAccessible(true);
//                                privateMethod.invoke(messageQueue);
//                            }
//
//                            XposedBridge.log("======================after "+Thread.currentThread().getName()+" enqueueMessage==============================");
//                            Message head = (Message) FlakeHunterUtils.getProperty(param.thisObject,"mMessages");
//                            if (head!=null){ //&& message.getCallback().getClass().getName().equals("android.view.Choreographer$FrameDisplayEventReceiver")
////                                XposedBridge.log("hook android.view.Choreographer$FrameDisplayEventReceiver message");
//                                XposedBridge.log(head.toString()+head.isAsynchronous());
//                                Message next = (Message) FlakeHunterUtils.getProperty(head,"next");
//                                while (next!=null){
//                                    XposedBridge.log(next.toString()+next.isAsynchronous());
//                                    next = (Message) FlakeHunterUtils.getProperty(next,"next");
//                                }
////                                XposedBridge.log(FlakeHunterUtils.getProperty(param.thisObject,"mMessages").toString());
////                                Thread.sleep(500);
//                            }
//                            XposedBridge.log("\n");
                        }

                    });


            XposedHelpers.findAndHookMethod(Handler.class,
                    "dispatchMessage", Message.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            FlakeHunterUtils.log(Thread.currentThread().getName() + ","
                                    + FlakeHunterUtils.getTimeNow() + ","
                                    + "dispatchMessage,"
                                    + FlakeHunterUtils.hash((Message) param.args[0]) + ","
                                    + "dispatchMessage beforeHookedMethod");
//                            XposedBridge.log("======================before dispatchMessage==============================");
//                            Message message = (Message) param.args[0];
//                            XposedBridge.log(message.toString()+message.isAsynchronous());
//                            XposedBridge.log("\n");
//
//                            if ((check_flag==1)&&message!=null && message.getCallback()!=null && message.getCallback().getClass().getName().equals("java.util.concurrent.FutureTask")) {
//                                XposedBridge.log("======================set Asynchronous==============================");
//                                message.setAsynchronous(true);
//                                check_flag = 0;
//                                @SuppressLint("SoonBlockedPrivateApi") Method privateMethod = MessageQueue.class.getDeclaredMethod("removeAllMessagesLocked");
//                                privateMethod.setAccessible(true);
//                                privateMethod.invoke(messageQueue);
//
//                            }

//                            if ((perform_flag==1)&&message!=null && message.getCallback()!=null && message.getCallback().getClass().getName().equals("java.util.concurrent.FutureTask")) {
//                                XposedBridge.log("======================set Block in MessageQueue==============================");
//                                delay_flag=1;
//                                message.setAsynchronous(true);
//                                perform_flag = 0;
//                                @SuppressLint("SoonBlockedPrivateApi") Method privateMethod = MessageQueue.class.getDeclaredMethod("removeAllMessagesLocked");
//                                privateMethod.setAccessible(true);
//                                privateMethod.invoke(messageQueue);
//                                start_time=System.currentTimeMillis();
//                            }
                        }

                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            FlakeHunterUtils.log(Thread.currentThread().getName() + ","
                                    + FlakeHunterUtils.getTimeNow() + ","
                                    + "dispatchMessage,"
                                    + FlakeHunterUtils.hash((Message) param.args[0]) + ","
                                    + "dispatchMessage afterHookedMethod");


//                            XposedBridge.log("======================after dispatchMessage==============================");
//                            Message message = (Message) param.args[0];
//                            XposedBridge.log(message.toString()+message.isAsynchronous());
//                            XposedBridge.log("\n");

//                            if ((perform_flag==1)&&message!=null && message.getCallback()!=null && message.getCallback().getClass().getName().equals("java.util.concurrent.FutureTask")) {
//                                XposedBridge.log("======================reset MessageQueue==============================");
//                                message.setAsynchronous(true);
//                                perform_flag = 0;
//                                @SuppressLint("SoonBlockedPrivateApi") Method privateMethod = MessageQueue.class.getDeclaredMethod("removeAllMessagesLocked");
//                                privateMethod.setAccessible(true);
//                                privateMethod.invoke(messageQueue);
//
//                                delay_flag = 1;
//                            }

                        }
                    });

            try {

//                XposedHelpers.findAndHookMethod("android.support.test.espresso.ViewInteraction", loadPackageParam.classLoader,
//                        "check", "android.support.test.espresso.ViewAssertion", new XC_MethodHook() {
//                            @Override
//                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                                check_flag = 1;
//                                XposedBridge.log("======================espresso check " + check_flag + "==============================");
//                                XposedBridge.log("\n");
//                            }
//                        });

//                XposedHelpers.findAndHookMethod("android.support.test.espresso.ViewInteraction", loadPackageParam.classLoader,
//                        "doPerform", "android.support.test.espresso.ViewAction", new XC_MethodHook() {
//                            @Override
//                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                                perform_flag = 1;
//                                XposedBridge.log("======================espresso doPerform " + perform_flag + "==============================");
//                                XposedBridge.log("\n");
//                            }
//                        });


                XposedHelpers.findAndHookMethod("android.support.test.espresso.base.UiControllerImpl", loadPackageParam.classLoader, "loopMainThreadUntilIdle", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        XposedBridge.log("======================loopMainThreadUntilIdle==============================");
                        return null;
                    }
                });
            } catch (Throwable e) {

            }


            try {
                XposedHelpers.findAndHookMethod("androidx.test.espresso.base.UiControllerImpl", loadPackageParam.classLoader, "loopMainThreadUntilIdle", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("======================loopMainThreadUntilIdle==============================");
//                        if (flag % 2 == 0) { // 5
                        if (flag % 2 == 0) {
//                        if (flag % 5 == 0) { 7
                            param.setResult(null);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });

//
//                XposedHelpers.findAndHookMethod("androidx.test.espresso.ViewInteraction", loadPackageParam.classLoader,
//                        "check", "androidx.test.espresso.ViewAssertion", new XC_MethodHook() {
//                            @Override
//                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                                check_flag = 1;
//                                XposedBridge.log("======================espresso check " + check_flag + "==============================");
//                                XposedBridge.log("\n");
//                            }
//                        });

//                XposedHelpers.findAndHookMethod("androidx.test.espresso.ViewInteraction", loadPackageParam.classLoader,
//                        "doPerform", "androidx.test.espresso.ViewInteraction.SingleExecutionViewAction", new XC_MethodHook() {
//                            @Override
//                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                                perform_flag = 1;
//                                XposedBridge.log("======================espresso doPerform " + perform_flag + "==============================");
//                                XposedBridge.log("\n");
//                            }
//                        });
            } catch (Throwable e) {

                XposedBridge.log(e.toString());
            }
//
//            TypeToken<List<Future<Object>>> typeToken = new TypeToken<List<Future<Object>>>() {
//            };
//            Class<List<Future<Object>>> listClass = (Class<List<Future<Object>>> )typeToken.getRawType();


//            XposedHelpers.findAndHookMethod("androidx.test.espresso.ViewInteraction", loadPackageParam.classLoader, "waitForAndHandleInteractionResults", List.class,new XC_MethodReplacement() {
//                        @Override
//                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//
//                            XposedBridge.log("======================drainMainThreadUntilIdle==============================");
//                            return null;
//                        }
//                    });


            try {
                XposedHelpers.findAndHookMethod("androidx.test.espresso.ViewInteraction", loadPackageParam.classLoader, "waitForAndHandleInteractionResults", List.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("======================drainMainThreadUntilIdle==============================");
//                        if (flag % 2 == 0) { // 5
                        if (flag % 2 == 0) {
//                        if (flag % 5 == 0) { 7
                            param.setResult(null);
                        }
                        flag += 1;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
//
            } catch (Throwable e) {
            }



//            Class<?> hookClass = loadPackageParam.classLoader.loadClass("androidx.test.internal.platform.os.ControlledLooper");
//            XposedBridge.hookAllMethods(hookClass,"drainMainThreadUntilIdle",new XC_MethodReplacement() {
//                @Override
//                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                    XposedBridge.log("======================drainMainThreadUntilIdle==============================");
//
//                    return null;
//                }
//            });


            FlakeHunterUtils.uiOperationListMap.get("get").forEach(uiOperation -> {
                Object[] parameterTypesAndCallback = uiOperation.getParamTypes().toArray();
                parameterTypesAndCallback = Arrays.copyOf(parameterTypesAndCallback, parameterTypesAndCallback.length + 1);
                parameterTypesAndCallback[parameterTypesAndCallback.length - 1] = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        String timeNow = FlakeHunterUtils.getTimeNow();
                        Object mID = FlakeHunterUtils.getProperty(param.thisObject, "mID");
                        mID = (mID == null) ? -1 : (int) mID;

//                        int hashCode = (param.thisObject == null)? -1:param.thisObject.hashCode();

                        FlakeHunterUtils.log(Thread.currentThread().getName() + ","
                                + timeNow + ","
                                + uiOperation.getMethodName() + ","
                                + mID + ","
                                + "get");
                    }

                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    }
                };

                try {
                    XposedHelpers.findAndHookMethod(uiOperation.getOperationClass(),
                            uiOperation.getMethodName(), parameterTypesAndCallback);
                } catch (Throwable e) {
//                    XposedBridge.log("hook error：" + e.toString());
                }
            });


            FlakeHunterUtils.uiOperationListMap.get("set").forEach(uiOperation -> {
                Object[] parameterTypesAndCallback = uiOperation.getParamTypes().toArray();
                parameterTypesAndCallback = Arrays.copyOf(parameterTypesAndCallback, parameterTypesAndCallback.length + 1);
                parameterTypesAndCallback[parameterTypesAndCallback.length - 1] = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        String timeNow = FlakeHunterUtils.getTimeNow();
                        Object mID = FlakeHunterUtils.getProperty(param.thisObject, "mID");
                        mID = (mID == null) ? -1 : (int) mID;
                        FlakeHunterUtils.log(Thread.currentThread().getName() + ","
                                + timeNow + ","
                                + uiOperation.getMethodName() + ","
                                + mID + ","
                                + "set");
                    }

                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    }
                };

                try {
                    XposedHelpers.findAndHookMethod(uiOperation.getOperationClass(),
                            uiOperation.getMethodName(), parameterTypesAndCallback);
                } catch (Throwable e) {
//                    XposedBridge.log("hook error：" + e.toString());
                }
            });

        }
    }

}