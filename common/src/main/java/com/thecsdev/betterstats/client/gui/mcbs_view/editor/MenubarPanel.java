package com.thecsdev.betterstats.client.gui.mcbs_view.editor;

import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * {@link McbsEditorGUI}'s menubar interface that appears at the top of the interface,
 * featuring controls and options for the user to interact with.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class MenubarPanel extends TElement
{
	// ================================================== ==================================================
	//                                       MenubarPanel IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull McbsEditor mcbsEditor;
	// ==================================================
	public MenubarPanel(@NotNull McbsEditor mcbsEditor) {
		this.mcbsEditor = Objects.requireNonNull(mcbsEditor);
	}
	// ==================================================
	public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		final var bb = getBounds();
		pencil.drawGuiSprite(BSSSprites.gui_editor_menubar_background(), bb.x, bb.y, bb.width, bb.height, -1);
	}
	public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
		final var bb = getBounds();
		pencil.drawGuiSprite(BSSSprites.gui_editor_menubar_foreground(), bb.x, bb.y, bb.width, bb.height, -1);
	}
	// --------------------------------------------------
	protected final @Override void initCallback()
	{
		//create and add the panel where the buttons will be placed
		final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
		panel.setBounds(getBounds().add(1, 1, -2, -2));
		panel.scrollPaddingProperty().set(0, MenubarPanel.class);
		add(panel);

		//initialize the menubar buttons
		for(final var item : BClientRegistries.MENUBAR_ITEM.entrySet())
		{
			//attempt to initialize the gui for a menubar item
			try {
				//current panel content bounding box
				final var pcbb = panel.getContentBounds();
				//the label
				final var label = requireNonNull(
						item.getValue().getDisplayName(), "Missing display name");
				//the button
				final var button = new Button();
				button.getLabel().setText(label);
				button.setBounds(
						pcbb.endX, pcbb.y,
						button.getLabel().fontProperty().get().width(label) + 10,
						panel.getBounds().height);
				button.contextMenuProperty().set(
						__ -> requireNonNull(
								item.getValue().createContextMenu(
										requireNonNull(__.getClient(), "Missing 'client' instance"),
										this.mcbsEditor),
								"Menubar item failed to produce a context menu, ID " + item.getKey()),
						MenubarPanel.class);
				button.eClicked.register(TElement::showContextMenu);
				panel.add(button);
			}
			//hold menubar items accountable for failures
			catch(Exception e) {
				throw new ReportedException(new CrashReport(
						"Something went wrong creating menubar item ID " + item.getKey(),
						e));
			}
		}
	}
	// ================================================== ==================================================
	//                                             Button IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TButtonWidget} implementation specifically for menubar items.
	 */
	private static final class Button extends TButtonWidget.Transparent {
		protected final @Override void initCallback() {
			getLabel().setBounds(getBounds());
			add(getLabel());
		}
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			if(!isHoveredOrFocused()) return;
			final var bb = getBounds();
			pencil.fillColor(bb.x, bb.y, bb.width, bb.height, 0x33ffffff);
		}
	}
	// ================================================== ==================================================
}
