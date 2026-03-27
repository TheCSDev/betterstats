package net.neoforged.fml.loading.moddiscovery;

import org.apache.maven.artifact.versioning.ArtifactVersion;

public final class ModInfo
{
	private ModInfo() {}
	public String getModId() { throw new AssertionError(); }
	public ArtifactVersion getVersion() { throw new AssertionError(); }
	public String getDisplayName() { throw new AssertionError(); }
}
