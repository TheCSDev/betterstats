package com.thecsdev.betterstats.api.mcbs.controller;

import com.thecsdev.common.properties.ObjectProperty;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This {@link Class} functions as the central controller in the MVC architecture,
 * overseeing the complete editor interface. It coordinates and manages all
 * {@link McbsEditorTab} instances contained within the editor, effectively
 * representing the entire graphical user interface (GUI).
 */
public final class McbsEditor
{
	// ==================================================
	private final Set<McbsEditorTab>            tabs       = new LinkedHashSet<>();
	private final ObjectProperty<McbsEditorTab> currentTab = new ObjectProperty<>();
	// ==================================================
	/**
	 * Returns the current {@link McbsEditorTab} being edited by this
	 * {@link McbsEditor}.
	 */
	public final @Nullable McbsEditorTab getCurrentTab() { return this.currentTab.get(); }
	// ==================================================
}
