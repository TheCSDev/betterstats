package com.thecsdev.betterstats.api.mcbs.view;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.view.tab.McbsEditorTabGUI;
import com.thecsdev.betterstats.mcbs.view.editor.MenubarPanel;
import com.thecsdev.betterstats.mcbs.view.editor.TabStripPanel;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
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
	// ================================================== ==================================================
	//                                      McbsEditorGUI IMPLEMENTATION
	// ================================================== ==================================================
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

		//create and add the tabbed document interface
		final var tdi = new TabbedDocumentInterface();
		tdi.setBounds(0, 20, getBounds().width, getBounds().height - 20);
		addRel(tdi);
	}
	// ================================================== ==================================================
	//                            TabbedDocumentInterface IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * A private inner class that represents the Tabbed Document Interface (TDI)
	 * area of the {@link McbsEditorGUI}.
	 */
	private final class TabbedDocumentInterface extends TElement
	{
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_editor_tab_background(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_editor_tab_foreground(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		protected final @Override void initCallback()
		{
			//create and add the editor tab list panel
			final var tablist = new TabStripPanel(McbsEditorGUI.this.mcbsEditor);
			tablist.setBounds(1, 1, getBounds().width - 2, 17);
			addRel(tablist);

			//create and add the editor-tab panel
			final var tab    = McbsEditorGUI.this.mcbsEditor.getCurrentTab();
			final var tabGui = McbsEditorTabGUI.createTabGui(tab);
			tabGui.setBounds(1, 18, getBounds().width - 2, getBounds().height - 19);
			addRel(tabGui);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
