package com.ivygames.morskoiboi.progress;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class ProgressManagerTest {

    private ProgressManager progressManager;

    @Mock
    private GoogleApiClientWrapper apiClient;
    private GameSettings settings = GameSettings.get();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        progressManager = new ProgressManager(apiClient, settings);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testProcessSuccessResult() throws Exception {

    }

    @Test
    public void testLoadProgress() throws Exception {
        progressManager.loadProgress();
    }

    @Test
    public void testIncrementProgress() throws Exception {

    }

    @Test
    public void testDebug_setProgress() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testResolveConflict() throws Exception {

    }
}