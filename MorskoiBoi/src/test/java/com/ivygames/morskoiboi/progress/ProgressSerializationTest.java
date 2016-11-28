package com.ivygames.morskoiboi.progress;

import com.ivygames.morskoiboi.Progress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ProgressSerializationTest {

    @Test
    public void SuccessfulParsingToAndFromJson() throws Exception {
        Progress progress1 = new Progress(1);
        String json = ProgressSerialization.toJson(progress1);
        Progress progress2 = ProgressSerialization.fromJson(json);

        assertThat(progress1.progress, is(progress2.progress));
    }

    @Test
    public void ParseInvalidProgress1() throws Exception {
        Progress progress = ProgressSerialization.parseProgress("[rank=1575175]");

        assertThat(progress.progress, is(1575175));
    }

    @Test
    public void ParseInvalidProgress2() throws Exception {
        Progress progress = ProgressSerialization.parseProgress("Progress [mRank=54397]");

        assertThat(progress.progress, is(54397));
    }

}