package io.github.flakreaper.timelord;

import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;

import com.google.common.collect.Lists;

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

public class HookDelayFunction implements IXposedHookLoadPackage {

    private final Map<String, Integer> staticIdMap = new ConcurrentHashMap<>();

    private long delay = 500;

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
                            String timeNow = TimeLordUtils.getTimeNow();
                            String threadinfos = "";

                            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                                threadinfos += stackTraceElement.getClassName() + stackTraceElement.getMethodName() + "\n";
                            }

                            if (!staticIdMap.containsKey(threadinfos)) {
                                staticIdMap.put(threadinfos, 0);
                            }

                            int index = staticIdMap.get(threadinfos) + 1;
                            staticIdMap.put(threadinfos, index);

                            String staticId = "" + TimeLordUtils.hash(threadinfos) + index;

                            TimeLordUtils.log(Thread.currentThread().getName() + ","
                                    + timeNow + ","
                                    + "enqueueMessage beforeHookedMethod,"
                                    + TimeLordUtils.hash((Message) param.args[0], (long) param.args[1]) + ","
                                    + TimeLordUtils.hash(threadinfos) + index);

//                            XposedBridge.log("======================before "+Thread.currentThread().getName()+" enqueueMessage==============================");
//                            Message head = (Message) TimeLordUtils.getProperty(param.thisObject,"mMessages");
//                            if (head!=null){ //&& message.getCallback().getClass().getName().equals("android.view.Choreographer$FrameDisplayEventReceiver")
////                                XposedBridge.log("hook android.view.Choreographer$FrameDisplayEventReceiver message");
//                                XposedBridge.log(head.toString()+head.isAsynchronous());
//                                Message next = (Message) TimeLordUtils.getProperty(head,"next");
//                                while (next!=null){
//                                    XposedBridge.log(next.toString()+next.isAsynchronous());
//                                    next = (Message) TimeLordUtils.getProperty(next,"next");
//                                }
////                                XposedBridge.log(TimeLordUtils.getProperty(param.thisObject,"mMessages").toString());
////                                Thread.sleep(500);
//                            }
//                            XposedBridge.log("\n");

//                            Message message = (Message) param.args[0];

                            if (TimeLordUtils.getStaticIdSet().contains(staticId)) {
                                long delayDuration = delay;

                                XposedBridge.log("Delay event hit!");
                                XposedBridge.log("Thread Name: " + Thread.currentThread().getName());
                                XposedBridge.log("Event Static ID : " + staticId);
                                XposedBridge.log("Event Info : " + param.args[0].toString());
                                XposedBridge.log("Current Time: " + timeNow);
                                XposedBridge.log("Delay Duration: " + delayDuration + "ms");
                                Thread.sleep(delayDuration);
                            }
                        }

                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            TimeLordUtils.log(Thread.currentThread().getName() + ","
                                    + TimeLordUtils.getTimeNow() + ","
                                    + "enqueueMessage,"
                                    + TimeLordUtils.hash((Message) param.args[0]) + ","
                                    + "enqueueMessage afterHookedMethod");
                        }

                    });


            XposedHelpers.findAndHookMethod(Handler.class,
                    "dispatchMessage", Message.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            TimeLordUtils.log(Thread.currentThread().getName() + ","
                                    + TimeLordUtils.getTimeNow() + ","
                                    + "dispatchMessage,"
                                    + TimeLordUtils.hash((Message) param.args[0]) + ","
                                    + "dispatchMessage beforeHookedMethod");
//                            XposedBridge.log("======================before dispatchMessage==============================");
//                            Message message = (Message) param.args[0];
//                            XposedBridge.log(message.toString()+message.isAsynchronous());
//                            XposedBridge.log("\n");
                        }

                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            TimeLordUtils.log(Thread.currentThread().getName() + ","
                                    + TimeLordUtils.getTimeNow() + ","
                                    + "dispatchMessage,"
                                    + TimeLordUtils.hash((Message) param.args[0]) + ","
                                    + "dispatchMessage afterHookedMethod");


//                            XposedBridge.log("======================after dispatchMessage==============================");
//                            Message message = (Message) param.args[0];
//                            XposedBridge.log(message.toString()+message.isAsynchronous());
//                            XposedBridge.log("\n");

                        }
                    });

            try {
                XposedHelpers.findAndHookMethod("androidx.test.espresso.base.UiControllerImpl", loadPackageParam.classLoader, "loopMainThreadUntilIdle", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("======================loopMainThreadUntilIdle==============================");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
            } catch (Throwable e) {

                XposedBridge.log(e.toString());
            }

            try {
                XposedHelpers.findAndHookMethod("androidx.test.espresso.ViewInteraction", loadPackageParam.classLoader, "waitForAndHandleInteractionResults", List.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("======================drainMainThreadUntilIdle==============================");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
//
            } catch (Throwable e) {
            }


            TimeLordUtils.uiOperationListMap.get("get").forEach(uiOperation -> {
                Object[] parameterTypesAndCallback = uiOperation.getParamTypes().toArray();
                parameterTypesAndCallback = Arrays.copyOf(parameterTypesAndCallback, parameterTypesAndCallback.length + 1);
                parameterTypesAndCallback[parameterTypesAndCallback.length - 1] = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        String timeNow = TimeLordUtils.getTimeNow();
                        Object mID = TimeLordUtils.getProperty(param.thisObject, "mID");
                        mID = (mID == null) ? -1 : (int) mID;

//                        int hashCode = (param.thisObject == null)? -1:param.thisObject.hashCode();

                        TimeLordUtils.log(Thread.currentThread().getName() + ","
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


            TimeLordUtils.uiOperationListMap.get("set").forEach(uiOperation -> {
                Object[] parameterTypesAndCallback = uiOperation.getParamTypes().toArray();
                parameterTypesAndCallback = Arrays.copyOf(parameterTypesAndCallback, parameterTypesAndCallback.length + 1);
                parameterTypesAndCallback[parameterTypesAndCallback.length - 1] = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        String timeNow = TimeLordUtils.getTimeNow();
                        Object mID = TimeLordUtils.getProperty(param.thisObject, "mID");
                        mID = (mID == null) ? -1 : (int) mID;
                        TimeLordUtils.log(Thread.currentThread().getName() + ","
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
