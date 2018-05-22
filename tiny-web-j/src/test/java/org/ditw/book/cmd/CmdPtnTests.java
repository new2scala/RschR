package org.ditw.book.cmd;

public class CmdPtnTests {

    public static void main(String[] args) {
        CashRegister cr = new CashRegister();
        CmdInvoker ci = new CmdInvoker();

        ci.executeCmd(new Purchase(cr, 10));
        ci.executeCmd(new Purchase(cr, 20));
        ci.executeCmd(new Refund(cr, 12));
        ci.executeCmd(new Purchase(cr, 15));

        ci.trace();
        cr.trace("After 3 purchases");
        ci.undo1();
        ci.trace();
        cr.trace("After undo 1");
        ci.undo1();
        ci.trace();
        cr.trace("After undo 1");

    }
}
