package net.jibini.mycelium;

import net.jibini.mycelium.thread.NamedThread;

public final class Mycelium
{
	public static void main(String[] args)
	{
		new NamedThread()
				.withName("MainThread")
				.withRunnable(() -> new MyceliumSpore().start())
				.start();
	}
}
