import java.util.ArrayList;

public class Ping implements CommandExecutor{
    @Override
    public String execute(ArrayList command) {
        return "+PONG\r\n";
    }
}
