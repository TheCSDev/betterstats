package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.mcbs.controller.tab.*;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.commonmc.api.client.gui.TElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class serves as the graphical user interface (GUI) for a specific {@link McbsEditorTab}.
 * As a subclass of {@link TElement}, it is tasked with initializing and rendering the user
 * interface associated with a particular {@link McbsEditorTab} within the context of an
 * {@link McbsEditorGUI}.
 *
 * @see McbsEditorTab
 * @see McbsEditorGUI
 */
@Environment(EnvType.CLIENT)
public abstract class McbsEditorTabGUI<T extends McbsEditorTab> extends TElement
{
	// ================================================== ==================================================
	//                                   McbsEditorTabGUI IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull T editorTab;
	// --------------------------------------------------
	private long lastSeenTabEditCount; //for keeping up-to-date with tab's changes
	// ==================================================
	public McbsEditorTabGUI(@NotNull T editorTab) throws NullPointerException {
		this.editorTab = Objects.requireNonNull(editorTab);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsEditorTab} instance associated with this GUI.
	 */
	public final @NotNull T getEditorTab() { return this.editorTab; }
	// ==================================================
	protected final @Override void tickCallback() {
		//if last seen tab edit count is out of date, we need to reinitialize
		if(this.lastSeenTabEditCount != this.editorTab.getEditCount())
			clearAndInit();
	}
	protected final @Override void initCallback() {
		if(!isEmpty()) throw new IllegalStateException("Already initialized. Please call #clearAndInit().");
		this.lastSeenTabEditCount = this.editorTab.getEditCount();
		initTabGuiCallback();
	}
	// ==================================================
	/**
	 * Subclasses should implement this method to initialize the GUI components
	 * specific to the associated {@link McbsEditorTab}. This method is called
	 * during the initialization phase of the GUI lifecycle.
	 * @see #initCallback()
	 */
	protected abstract void initTabGuiCallback();
	// ================================================== ==================================================
	//                                            FACTORY IMPLEMENTATIONS
	// ================================================== ==================================================
	private static final Map<Class<?>, Function<?, ?>> FACTORIES = new HashMap<>();
	// ==================================================
	/**
	 * Registers a factory {@link Function} for creating {@link McbsEditorTabGUI} instances
	 * corresponding to a specific type of {@link McbsEditorTab}.
	 *
	 * @param tabType The class type of the {@link McbsEditorTab} for which the factory is
	 *                being registered.
	 * @param tabGuiFactory A function that takes an instance of the specified
	 *                      {@link McbsEditorTab} type and returns a corresponding
	 *                      {@link McbsEditorTabGUI} instance.
	 * @param <T> The type of {@link McbsEditorTab} for which the factory is being registered.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final <T extends McbsEditorTab> void registerFactory(
			@NotNull Class<T> tabType,
			@NotNull Function<@NotNull T, @NotNull McbsEditorTabGUI<T>> tabGuiFactory)
			throws NullPointerException
	{
		//argument not null requirements
		Objects.requireNonNull(tabType);
		Objects.requireNonNull(tabGuiFactory);

		//register the factory
		FACTORIES.put(tabType, tabGuiFactory);
	}

	/**
	 * Creates a new {@link McbsEditorTabGUI} instance for the given {@link McbsEditorTab}
	 * using the registered factory function.
	 *
	 * @param editorTab The {@link McbsEditorTab} instance for which the GUI is to be created.
	 * @param <T> The type of the {@link McbsEditorTab}.
	 * @return A new {@link McbsEditorTabGUI} instance corresponding to the provided
	 *         {@link McbsEditorTab}.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @throws IllegalStateException If no factory is registered for the type of the
	 *                               provided {@link McbsEditorTab}.
	 */
	public static final <T extends McbsEditorTab> @NotNull McbsEditorTabGUI<T> createTabGui(
			@Nullable T editorTab) throws NullPointerException
	{
		//argument not null requirement
		Objects.requireNonNull(editorTab);

		//get the factory for the given tab type
		@SuppressWarnings("unchecked")
		final Function<@NotNull T, @NotNull McbsEditorTabGUI<T>> factory =
				(Function<@NotNull T, @NotNull McbsEditorTabGUI<T>>) FACTORIES.get(editorTab.getClass());
		if(factory == null)
			throw new IllegalStateException(
					"No editor tab GUI factory registered for tab type: " +
							editorTab.getClass().getName());

		//create and return the tab GUI
		return Objects.requireNonNull(
				factory.apply(editorTab),
				"Editor tab GUI factory returned 'null' for " + editorTab);
	}
	// ==================================================
	/**
	 * Registers internal {@link McbsEditorTabGUI} factories.
	 */
	public static final @ApiStatus.Internal void bootstrap() {
		registerFactory(McbsEditorHomepageTab.class, McbsEditorHomepageTabGUI::new);
		registerFactory(McbsEditorNullTab.class,     McbsEditorNullTabGUI::new);
		registerFactory(McbsEditorFileTab.class,     McbsEditorFileTabGUI::new);
		registerFactory(McbsEditorSettingsTab.class, McbsEditorSettingsTabGUI::new);
	}
	// ================================================== ==================================================
}
