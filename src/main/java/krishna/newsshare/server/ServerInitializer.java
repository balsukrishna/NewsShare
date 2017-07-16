package krishna.newsshare.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Initializes the server on port 8080
 * @author krishna
 *
 */
public class ServerInitializer {
    static final int PORT = 80;
    private static final Logger log = LoggerFactory.getLogger(ServerInitializer.class);

    public static void main(String[] args) throws Exception {

    	//Boss group responsible for listening connections
    	//Use single thread
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //Worker group responsible for handling all the logic,io for
        //established connections
        
        //Use twice the number of cores
        int cores = Runtime.getRuntime().availableProcessors();
        EventLoopGroup workerGroup = new NioEventLoopGroup(cores *2);
    
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new NewClientInitializer());

            Channel ch = b.bind(PORT).sync().channel();

            log.info("Open your web browser and navigate to http://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}