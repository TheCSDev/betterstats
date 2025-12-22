package com.thecsdev.betterstats.api.mcbs.view;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.client.gui.mcbs_view.editor.MenubarPanel;
import com.thecsdev.betterstats.client.gui.mcbs_view.editor.TabContentPanel;
import com.thecsdev.betterstats.client.gui.mcbs_view.editor.TabStripPanel;
import com.thecsdev.commonmc.api.client.gui.TElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * {@link TElement} implementation that holds all of {@link BetterStatsScreen}'s
 * graphical user interface.
 */
@Environment(EnvType.CLIENT)
public final class McbsEditorGUI extends TElement
{
	// ==================================================
	private final @NotNull McbsEditor mcbsEditor;
	// --------------------------------------------------
	private long lastSeenEditCount; //for keeping up to date with controller's changes
	// ==================================================
	public McbsEditorGUI(@NotNull McbsEditor mcbsEditor) throws NullPointerException {
		this.mcbsEditor = requireNonNull(mcbsEditor);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsEditor} instance associated with this GUI.
	 */
	public final @NotNull McbsEditor getMcbsEditor() { return this.mcbsEditor; }
	// ==================================================
	protected final @Override void tickCallback() {
		//if last seen edit count is out of date, we need to reinitialize
		if(this.lastSeenEditCount != this.mcbsEditor.getEditCount())
			clearAndInit();
	}

	protected final @Override void initCallback()
	{
		//when reinitializing, we're up-to-date, so clear any "dirtiness" flags
		this.lastSeenEditCount = this.mcbsEditor.getEditCount();

		//create and add the menubar
		final var menubar = new MenubarPanel(this.mcbsEditor);
		menubar.setBounds(0, 0, getBounds().width, 17);
		addRel(menubar);

		//create and add the editor tab list panel
		final var tablist = new TabStripPanel(this.mcbsEditor);
		tablist.setBounds(0, 20, getBounds().width, 17);
		addRel(tablist);

		//create and add the editor-tab panel
		final var editorTab = new TabContentPanel(this.mcbsEditor);
		editorTab.setBounds(0, 36, getBounds().width, getBounds().height - 36);
		addRel(editorTab);
	}
	// ==================================================
}
