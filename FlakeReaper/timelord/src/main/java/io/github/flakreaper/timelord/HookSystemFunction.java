package io.github.flakreaper.timelord;

import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HookSystemFunction implements IXposedHookZygoteInit {

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {


        XposedHelpers.findAndHookMethod(MessageQueue.class,
                "enqueueMessage",Message.class,long.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

                    }
                });



        XposedHelpers.findAndHookMethod(Handler.class,
                "dispatchMessage",Message.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    }



                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    }
                });




    }
}
