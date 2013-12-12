package de.matzefratze123.api.reflect;

public enum MCPackage {
	
	CRAFT_PACKAGE {

		@Override
		public String toString() {
			return "org.bukkit.craftbukkit." + version;
		}
		
	},
	NMS_PACKAGE {

		@Override
		public String toString() {
			return "net.minecraft.server." + version;
		}
		
	};
	
	private static String version;
	
	static {
		if (version == null) {
			Package[] knownPackages = Package.getPackages();
			
			for (Package pack : knownPackages) {
				if (pack.getName().startsWith("net.minecraft.server")) {
					String[] parts = pack.getName().split("\\.");
					if (parts.length < 4) {
						continue;
					}
					
					version = parts[3];
					break;
				}
			}
		}
	}
	
	public abstract String toString();
	
	
	
}
