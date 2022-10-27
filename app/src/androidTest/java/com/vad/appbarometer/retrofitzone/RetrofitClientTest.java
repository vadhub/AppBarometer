package com.vad.appbarometer.retrofitzone;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;


@RunWith(AndroidJUnit4ClassRunner.class)
public class RetrofitClientTest {

    @Test
    public void getPressure_success() throws PackageManager.NameNotFoundException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        ApplicationInfo applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);

        RetrofitClient.getInstance().getJsonApi().getData(10.233f, 12.233f, applicationInfo.metaData.getString("keyValue"))
                .subscribe(Assert::assertNotNull);
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.reset();
        RxJavaPlugins.reset();
    }
}