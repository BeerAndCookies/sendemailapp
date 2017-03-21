package com.example.abdussamed.sendemailapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends Activity implements View.OnClickListener{

    Session session;
    ProgressDialog progressDialog;
    EditText sender_edT, pass_edT, subject_edT, msg_edT;
    String sender_str, pass_str, subject_str, msg_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.btn_submit);
        sender_edT = (EditText) findViewById(R.id.e_from);
        pass_edT = (EditText) findViewById(R.id.e_pass);
        subject_edT = (EditText) findViewById(R.id.e_sub);
        msg_edT = (EditText) findViewById(R.id.e_tex);

        login.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        sender_str = sender_edT.getText().toString();
        pass_str = pass_edT.getText().toString();
        subject_str = subject_edT.getText().toString();
        msg_str = msg_edT.getText().toString();

        Properties props = new Properties();
        //make the user select the account, or find out from input string which is better
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            /**
             * Called when password authentication is needed.  Subclasses should
             * override the default implementation, which returns null. <p>
             * <p>
             * Note that if this method uses a dialog to prompt the user for this
             * information, the dialog needs to block until the user supplies the
             * information.  This method can not simply return after showing the
             * dialog.
             *
             * @return The PasswordAuthentication collected from the
             * user, or null if none is provided.
             */
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender_str, pass_str);
            }
        });

        progressDialog = ProgressDialog.show(MainActivity.this, "","Sending Email...", true);
        RetrieveFeedTask retrieveFeedTask = new RetrieveFeedTask();
        retrieveFeedTask.execute();

    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String>{


        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... params) {
            try{

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender_str));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("rspf.getraenke@gmail.com"));
                message.setSubject(subject_str);
                message.setContent(msg_str, "text/html; charset=utf-8");
                //message.setText(msg_str);

                Transport.send(message);
            }catch(MessagingException e){
                e.printStackTrace();
            }catch( Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            sender_edT.setText("");
            pass_edT.setText("");
            msg_edT.setText("");
            subject_edT.setText("");
            Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_SHORT).show();
        }

    }
}
