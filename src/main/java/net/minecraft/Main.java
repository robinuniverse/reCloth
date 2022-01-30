package net.minecraft;

import net.minecraft.cloth.file.AdvancementCriterionLoader;
import net.minecraft.core.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.minecraft.Globals.*;

/**
 * Custom Main class for stitch & convience
 *
 * @author Luminoso-256
 */
public class Main {


    public static final Logger logger = Logger.getLogger("Minecraft");
    public static final MinecraftServer minecraftServer = new MinecraftServer(); //Get a reference to our lovely MC server class

    public static void main(String[] args) {
        Random random = new Random();
        logger.info("Welcome to " + VERSION_STRING + " - " + WELCOME_MSG[random.nextInt(WELCOME_MSG.length)]);
        if (IS_PREVIEW) {
            logger.warning("*********Warning: This is a pre-release build of Cloth. Issues may arise. Targeting feature: " + TARGET_FEATURE);
        }

        logger.info("[Cloth] Checking world seed");
        PropertyManager propertyManager = new PropertyManager(new File("server.properties"));
        propertyManager.getLongProperty("seed", 1l);

        logger.info("[Cloth] Cloth init complete. Deffering to MinecraftServer class ");
        try {
            // net.minecraft.server.MinecraftServer.main(args);
            minecraftServer.advancementCriterion = (HashMap<String, String>) AdvancementCriterionLoader.loadAdvancementCriterion();
            try {
                if (!GraphicsEnvironment.isHeadless() && (args.length <= 0 || !args[0].equals("nogui"))) {
                    ServerGUI.initGui(minecraftServer);
                }
                (new ThreadServerApplication("Server thread", minecraftServer)).start();
            } catch (Exception exception) {
                logger.log(Level.SEVERE, "Failed to start the minecraft server", exception);
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, null, t);
        }
    }
}
