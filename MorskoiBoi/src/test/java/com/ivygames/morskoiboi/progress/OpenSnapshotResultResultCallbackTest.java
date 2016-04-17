package com.ivygames.morskoiboi.progress;

import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class OpenSnapshotResultResultCallbackTest {

    private OpenSnapshotResultResultCallback callback;
    private GameSettings settings = Dependencies.getSettings();

    @Mock
    private SnapshotOpenResultListener listener;

    @Before
    public void setUp() {
        initMocks(this);
        callback = new OpenSnapshotResultResultCallback(settings, listener);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testOnResult() {

    }
}