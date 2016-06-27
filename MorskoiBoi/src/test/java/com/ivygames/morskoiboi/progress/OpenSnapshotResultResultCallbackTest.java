package com.ivygames.morskoiboi.progress;

import com.ivygames.morskoiboi.GameSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.MockitoAnnotations.initMocks;

public class OpenSnapshotResultResultCallbackTest {

    @Mock
    private SnapshotOpenResultListener listener;

    @Before
    public void setUp() {
        GameSettings settings = Mockito.mock(GameSettings.class);
        initMocks(this);
        OpenSnapshotResultResultCallback callback = new OpenSnapshotResultResultCallback(settings.getProgress(), listener);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testOnResult() {

    }
}