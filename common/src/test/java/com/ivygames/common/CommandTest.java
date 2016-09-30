package com.ivygames.common;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CommandTest {

    private MyCommand command = new MyCommand();

    @Test
    public void InitiallyCommandIsNotExecuted() {
        assertThat(command.executed(), is(false));
    }

    @Test
    public void AfterCommandRuns__ItIsExecuted() {
        command.run();

        assertThat(command.executed(), is(true));
    }

    @Test
    public void WhenCommandRuns__ItIsActuallyExecuting() {
        command.run();

        assertThat(command.executed, is(true));
    }

    @Test
    public void WhenThereIsNextCommand__ItIsExecutedAfterThisOne() {
        MyCommand nextCommand = new MyCommand();
        command.setNextCommand(nextCommand);

        command.run();

        assertThat(command.order < nextCommand.order, is(true));
        assertThat(nextCommand.executed(), is(true));
    }

    private static class MyCommand extends Command {
        private static int counter;

        private boolean executed;
        int order;

        @Override
        protected void execute() {
            executed = true;
            order = counter++;
        }
    }
}