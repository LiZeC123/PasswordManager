package lizec.lizec.tlock.net.command;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface CommonCmd {
    boolean doCmd(ObjectInputStream in, ObjectOutputStream out);
}
