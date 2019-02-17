package yuudaari.soulus.common.network.packet.client;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.Config;

public class SendConfig implements IMessage {

	private final Map<String, String> configs;

	public SendConfig () {
		configs = new HashMap<>();
	}

	public SendConfig (Map<String, String> configs) {
		this.configs = configs;
	}

	@Override
	public void toBytes (ByteBuf buf) {
		// write map size
		buf.writeInt(configs.size());
		for (Entry<String, String> entry : configs.entrySet()) {
			// write key
			buf.writeInt(entry.getKey().length());
			buf.writeBytes(entry.getKey().getBytes());
			// write val
			buf.writeInt(entry.getValue().length());
			buf.writeBytes(entry.getValue().getBytes());
		}
	}

	@Override
	public void fromBytes (ByteBuf buf) {
		for (int l = buf.readInt(); l > 0; l--) {
			String key = buf.readBytes(buf.readInt()).toString(Charset.defaultCharset());
			String val = buf.readBytes(buf.readInt()).toString(Charset.defaultCharset());
			configs.put(key, val);
		}
	}

	public static class Handler implements IMessageHandler<SendConfig, IMessage> {

		@Override
		public IMessage onMessage (SendConfig message, MessageContext ctx) {
			Config config = Config.INSTANCES.get(Soulus.MODID);
			config.SERVER_CONFIGS.clear();
			config.SERVER_CONFIGS.putAll(message.configs);

			Soulus.reloadConfigs(true, false);

			return null;
		}
	}
}
