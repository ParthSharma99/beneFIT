package tech.iosd.benefit.Chat;

/**
 * Created by SAM33R on 23-06-2018.
 */

import android.app.Application;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import tech.iosd.benefit.Model.DatabaseHandler;

import static tech.iosd.benefit.Utils.Constants.CHAT_SERVER_URL;

public class ChatApplication extends Application {

    private Socket mSocket;
    private String tokenS;

    public Socket getSocket() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        tokenS = db.getUserToken();
        if (mSocket == null) {
            IO.Options opts = new IO.Options();
            opts.query = "token=" + tokenS;
            try {
                mSocket = IO.socket(CHAT_SERVER_URL, opts);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            Log.w("token", tokenS);
        }
        return mSocket;
    }
}
