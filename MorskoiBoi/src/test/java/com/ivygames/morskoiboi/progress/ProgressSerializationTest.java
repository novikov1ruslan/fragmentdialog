package com.ivygames.morskoiboi.progress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ProgressSerializationTest {

    @Test
    public void SuccessfulParsingToAndFromJson() throws Exception {
        String json = ProgressSerialization.toJson(1);

        assertThat(ProgressSerialization.fromJson(json), is(1));
    }

    @Test
    public void ParseInvalidProgress1() throws Exception {
        int progress = ProgressSerialization.parseProgress("[rank=1575175]");

        assertThat(progress, is(1575175));
    }

    @Test
    public void ParseInvalidProgress2() throws Exception {
        int progress = ProgressSerialization.parseProgress("Progress [mRank=54397]");

        assertThat(progress, is(54397));
    }

}