package krishna.newsshare.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import krishna.newsshare.datastructure.TopicRepoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Initializes and setup pipeline for every new connection
 * @author krishna
 *
 */
@Sharable
public class NewClientInitializer extends ChannelInitializer<SocketChannel> {
	private static final Logger log  = LoggerFactory.getLogger(NewClientInitializer.class);
	private static final String INDEX_PAGE = "src/main/javascript/index.html";
	private String indexContent;
	private UpdateCommitter updateCommitter;
	
	public NewClientInitializer() {
		indexContent = readIndexPage();
		updateCommitter = new UpdateCommitter(new TopicRepoImpl());
	}
	
	@Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new RequestUpgrader(indexContent));
        pipeline.addLast(new WSFrameHandler());
        pipeline.addLast(updateCommitter);
    }
	
    private String readIndexPage() {
    	BufferedReader buf = null;
    	try {
    		InputStream is = new FileInputStream(INDEX_PAGE);
        	buf = new BufferedReader(new InputStreamReader(is));
        	String line = buf.readLine();
        	StringBuilder sb = new StringBuilder();
        	while(line != null){
        		sb.append(line).append("\n");
        		line = buf.readLine();
        	}
        	String fileAsString = sb.toString(); 
        	return fileAsString;
    	} catch(Exception ex) {
    		log.error("Error while reading index page",ex);
    	} finally {
    		if(buf != null) {
    			try {
					buf.close();
				} catch (IOException e) {
					log.error("",e);
				}
    		}
    	}
    	//Return Default
    	return "<html><head><title>NewsShare Server Error!</title></head>";
	}
}