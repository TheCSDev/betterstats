/**
 * This package implements the Model-View-Controller (MVC) architectural pattern, and is responsible
 * for displaying data that is present in models.
 * <p>
 * Views act as the visual representation of the data, while controllers handle user input and
 * interactions, updating the models and views accordingly.
 * <p>
 * <u><b>Important note:</b></u><br>
 * This package contains GUI APIs are that available only on the <b>client-side</b>! Do NOT attempt
 * to access these APIs from a server-side (be it a dedicated or an integrated server)! Use the game
 * client's render thread.
 *
 * @see com.thecsdev.betterstats.api.mcbs
 */
@Environment(EnvType.CLIENT)
package com.thecsdev.betterstats.api.mcbs.view;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;