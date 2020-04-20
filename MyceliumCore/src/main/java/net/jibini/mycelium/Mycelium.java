package net.jibini.mycelium;

import net.jibini.mycelium.thread.NamedThread;

public final class Mycelium
{
	public static final MyceliumSpore SPORE = new MyceliumSpore();
	
	public static void main(String[] args)
	{
		new NamedThread()
				.withName("MainThread")
				.withRunnable(() -> SPORE.start())
				.start();
	}
}
