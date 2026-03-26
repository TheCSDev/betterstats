/**
 * This package and its subpackages implement the Model-View-Controller (MVC) architectural pattern
 * to effectively handle the creation, storage, retrieval, and presentation of player statistics data.
 * <p>
 * The "Model" component is responsible for managing the raw data, the "View" component handles the
 * graphical user interface for displaying the data, and the "Controller" component facilitates
 * user interactions and data manipulation.
 * <p>
 * <u><b>Important note:</b></u><br>
 * The {@code .view.*} package contains GUI APIs that are available only on the <b>client-side</b>!
 * Do NOT attempt to access those APIs from a server-side (be it a dedicated or an integrated
 * server)! Use the game client's render thread.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Wikipedia</a>
 */
package com.thecsdev.betterstats.api.mcbs;
