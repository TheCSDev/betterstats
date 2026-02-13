package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorHomepageTab;
import com.thecsdev.betterstats.client.gui.panel.BSCreditsPanel;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

/**
 * The GUI implementation for {@link McbsEditorHomepageTab}.
 */
@Environment(EnvType.CLIENT)
public final class McbsEditorHomepageTabGUI extends McbsEditorTabGUI<McbsEditorHomepageTab>
{
	// ==================================================
	private static long LAST_REFRESH = System.currentTimeMillis();
	// ==================================================
	public McbsEditorHomepageTabGUI(@NotNull McbsEditorHomepageTab editorTab) throws NullPointerException {
		super(editorTab);
	}
	// ==================================================
	protected final @Override void initTabGuiCallback()
	{
		//refresh if last refresh was a while ago
		if(Math.abs(System.currentTimeMillis() - LAST_REFRESH) > (1000 * 60 * 5)) {
			LAST_REFRESH = System.currentTimeMillis();
			getEditorTab().refresh();
		}

		//news panel
		final var news = new BSCreditsPanel(getEditorTab().getNewsAsync());
		add(news);
		news.setBounds(new UDim2(0, 10, 0, 10), new UDim2(0.7, -38, 1, -20));

		final var newsBB      = news.getBounds();
		final var scroll_news = new TScrollBarWidget.Flat(news);
		scroll_news.setBounds(newsBB.endX - 1, newsBB.y, 8, newsBB.height);
		add(scroll_news);

		//credits panel
		final var credits = new BSCreditsPanel(getEditorTab().getCreditsAsync());
		add(credits);
		credits.setBounds(new UDim2(0.7, -10, 0, 10), new UDim2(0.3, -7, 1, -20));

		final var credBB         = credits.getBounds();
		final var scroll_credits = new TScrollBarWidget.Flat(credits);
		scroll_credits.setBounds(credBB.endX - 1, credBB.y, 8, credBB.height);
		add(scroll_credits);
	}
	// ==================================================
}
