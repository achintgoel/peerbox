package network;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;

public class MessageServerHandler extends SimpleChannelUpstreamHandler {
	protected MessageListener downstreamMessageListener;
	final protected String protocol;
	
	public static MessageSender startUDPServer(int port, final MessageListener messageListener) {
        DatagramChannelFactory f =
            new NioDatagramChannelFactory(Executors.newCachedThreadPool());

        ConnectionlessBootstrap b = new ConnectionlessBootstrap(f);

        // Configure the pipeline factory.
        b.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new StringEncoder(CharsetUtil.ISO_8859_1),
                        new StringDecoder(CharsetUtil.ISO_8859_1),
                        new MessageServerHandler(messageListener, "udp"));
            }
        });

        // Enable broadcast
        b.setOption("broadcast", "false");

        // Allow packets as large as up to 1024 bytes (default is 768).
        // You could increase or decrease this value to avoid truncated packets
        // or to improve memory footprint respectively.
        //
        // Please also note that a large UDP packet might be truncated or
        // dropped by your router no matter how you configured this option.
        // In UDP, a packet is truncated or dropped if it is larger than a
        // certain size, depending on router configuration.  IPv4 routers
        // truncate and IPv6 routers drop a large packet.  That's why it is
        // safe to send small packets in UDP.
        b.setOption(
                "receiveBufferSizePredictorFactory",
                new FixedReceiveBufferSizePredictorFactory(1024));

        // Bind to the port and start the service.
        Channel c = b.bind(new InetSocketAddress(port));
        return new MessageSender(c);
	}
	
	protected MessageServerHandler(MessageListener downstreamMessageListener, String protocol) {
		this.downstreamMessageListener = downstreamMessageListener;
		this.protocol = protocol;
	}
	
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    	String data = (String) e.getMessage();
    	InetSocketAddress addr = (InetSocketAddress) e.getRemoteAddress();
    	URI uri;
    	try {
			uri = new URI(protocol + "://" + addr.getHostName() + ":" + addr.getPort());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			uri = null;
		}
    	
    	IncomingMessage message = new IncomingMessage(e, uri, data);
    	downstreamMessageListener.onMessage(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        e.getCause().printStackTrace();
        // We don't close the channel because we can keep serving requests.
    }
}