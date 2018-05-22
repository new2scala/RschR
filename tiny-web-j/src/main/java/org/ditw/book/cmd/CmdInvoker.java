package org.ditw.book.cmd;


import java.util.LinkedList;
import java.util.List;

public class CmdInvoker {
    private final List<ICmd> cmdHistory = new LinkedList<>();

    public void executeCmd(ICmd cmd) {
        cmd.Execute();
        cmdHistory.add(0, cmd);
    }

    public void undo1() {
        ICmd firstCmd = cmdHistory.get(0);
        firstCmd.Undo();
        cmdHistory.remove(0);
    }

    public void undoAll() {
        for (ICmd cmd : cmdHistory) {
            cmd.Undo();
        }
        cmdHistory.clear();
    }

    public void trace() {
        System.out.println(
            String.format("Cmd count: %d\n", cmdHistory.size())
        );
    }

}
