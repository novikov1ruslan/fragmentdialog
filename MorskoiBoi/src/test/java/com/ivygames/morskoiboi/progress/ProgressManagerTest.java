package com.ivygames.morskoiboi.progress;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ProgressManagerTest {

    private ProgressManager progressManager;

    @Mock
    private PendingResult<Snapshots.OpenSnapshotResult> pendingResult;

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
//        Mockito.when(apiClient.openAsynchronously("snapshotName", )
        Answer answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                Snapshots.OpenSnapshotResult result = mock(Snapshots.OpenSnapshotResult.class);
                Status status = mock(Status.class);
                when(result.getStatus()).thenReturn(status);
//                when(result.getSnapshot()).thenReturn(null);
                when(status.isSuccess()).thenReturn(true);
                ((ResultCallback)arguments[1]).onResult(result);
                return null;
            }
        };
        doAnswer(answer).when(apiClient).openAsynchronously(anyString(), any(ResultCallback.class));
        progressManager.loadProgress();
        verify(progressManager).processSuccessResult(result.getSnapshot());)
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