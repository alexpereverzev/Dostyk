package litebonus.dostyk.push;

import android.os.AsyncTask;
import android.os.Handler;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import litebonus.dostyk.interfaces.ServerMethods;

/**
 * Created by Alexander on 20.11.2014.
 */
public class MessageConsumer extends IConnectToRabbitMQ {

    Handler handler = new Handler();

    public MessageConsumer(String server, String exchange, String exchangeType) {
        super(server, exchange, exchangeType);
    }

    //The Queue name for this consumer
    private String mQueue;
    private QueueingConsumer MySubscription;

    //last message to post back
    private byte[] mLastMessage;

    // An interface to be implemented by an object that is interested in messages(listener)
    public interface OnReceiveMessageHandler {
        public void onReceiveMessage(byte[] message);
    }


    //A reference to the listener, we can only have one at a time(for now)
    private OnReceiveMessageHandler mOnReceiveMessageHandler;

    /**
     * Set the callback for received messages
     *
     * @param handler The callback
     */
    public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler) {
        mOnReceiveMessageHandler = handler;
    }

    ;

    private Handler mMessageHandler = new Handler();
    private Handler mConsumeHandler = new Handler();

    // Create runnable for posting back to main thread
    final Runnable mReturnMessage = new Runnable() {
        public void run() {
            mOnReceiveMessageHandler.onReceiveMessage(mLastMessage);
        }
    };

    final Runnable mConsumeRunner = new Runnable() {
        public void run() {
            Consume();
        }
    };

    /**
     * Create Exchange and then start consuming. A binding needs to be added before any messages will be delivered
     */
    @Override
    public boolean connectToRabbitMQ() {
        if (flag) {



            String corrId = java.util.UUID.randomUUID().toString();

            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .contentType("application/json")
                    .replyTo(replyQueueName)
                    .build();


            try {
                if (input_json != null) {
                    channel.basicPublish("", requestQueueName, props, input_json.toString().getBytes());
                    // else channel.basicPublish("", requestQueueName, props, obj.toString().getBytes());
                    Running = true;
                    mConsumeHandler.post(mConsumeRunner);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }


    public void AddBinding(String routingKey) {
        try {
            mModel.queueBind(mQueue, mExchange, routingKey);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private JSONObject input_json;

    public void sendMessage(JSONObject jsonObject) {
        String[] param = new String[2];
        //   input_json = jsonObject;
        param[1] = jsonObject.toString();
        param[0] = ServerMethods.Rabbit;
        new SendTask().execute(param);
    }

    public void sendClear_message(JSONObject jsonObject) {
        String[] param = new String[1];

        param[0] = jsonObject.toString();
        new ClearMessage().execute(param);
    }


    public void sendMessageZone(JSONObject jsonObject) {
        String[] param = new String[2];
        param[1] = jsonObject.toString();
        param[0] = ServerMethods.Rabbit;
        new SendTaskZone().execute(param);
    }


    private void Consume() {
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (Running) {

                    QueueingConsumer.Delivery delivery;
                    try {
                        delivery = consumer.nextDelivery();
                        if (delivery != null) {
                            mLastMessage = delivery.getBody();

                            mMessageHandler.post(mReturnMessage);
                        }

                        channel.basicAck(delivery.getEnvelope()
                                .getDeliveryTag(), false);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (ShutdownSignalException sse) {

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        thread.start();

    }

    public void dispose() {
        Running = false;

        try {
            if (mConnection != null)
                mConnection.close();
            if (channel != null)
                channel.abort();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    private Connection connection;
    private Channel channel;
    private String requestQueueName = "device-register";
    private String requestQueueNameZone = "device-zone-changed";
    private String clear_message = "device-clear-message-history";
    private String replyQueueName;
    private QueueingConsumer consumer;



    class SendTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            ConnectionFactory factory = new ConnectionFactory();
            try {
                factory.setUri(urls[0]);
                connection = factory.newConnection();
                channel = connection.createChannel();
                replyQueueName = channel.queueDeclare().getQueue();
                consumer = new QueueingConsumer(channel);
                channel.basicConsume(replyQueueName, false, consumer);
              //  channel.basicQos(3);
                String corrId = java.util.UUID.randomUUID().toString();
                AMQP.BasicProperties props = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(corrId)
                        .contentType("application/json")
                        .replyTo(replyQueueName)
                        .build();
                try {
                    channel.basicPublish("", requestQueueName, props, urls[1].getBytes());
                    Running = true;
                    mConsumeHandler.post(mConsumeRunner);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    class SendTaskZone extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls) {
            String corrId = java.util.UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .contentType("application/json")
                    .build();
            try {

                if (channel.isOpen())
                    channel.basicPublish("", requestQueueNameZone, props, urls[1].getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class ClearMessage extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            String corrId = java.util.UUID.randomUUID().toString();
            byte [] send= urls[0].getBytes();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .contentType("application/json")
                    .build();
            try {
                if (channel.isOpen())
                    channel.basicPublish("", clear_message, props, send);

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    private static boolean flag = false;
}
