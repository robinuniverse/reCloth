package net.minecraft.cloth.plugins.stich;


import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static net.minecraft.Globals.STITCH_HOOK_VERSION;

/**
 * Core stitch system
 *
 * @author Luminoso-256
 * <p>
 * Steps:
 * 1. Search plugins directory, find all plugin files
 * 2. Note each plugin files hooks and register them to the apropriate hook list
 * 3. Listen to each hook, and on call defer to the plugin
 */
public class StitchLoader {
    public Globals stichGlobals;
    //Hook Lists
    public List<String> OnServerInitHook = new ArrayList<String>();
    public List<String> OnBlockDestroyHook = new ArrayList<String>();
    public List<String> OnBlockCreateByHook = new ArrayList<String>();

    //HOOK CALL
    public List<String> OnServerTickHook = new ArrayList<String>();


    public StitchLoader(Globals StichGlobals) {
        stichGlobals = StichGlobals;
    }

    public void RegisterAllPlugins() {
        Logger logger = Logger.getLogger("Minecraft");
        File PluginsDirectory = new File("plugins");
        String[] PluginFiles = PluginsDirectory.list();
        if (PluginFiles != null) {
            for (int i = 0; i < PluginFiles.length; i++) {
                logger.info("[Stitch] Found File: " + PluginFiles[i]);

                try {
                    RegisterPlugin(PluginFiles[i]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            System.out.println(OnServerTickHook);
        }
    }

    /**
     * Takes plugin file, grabs respective json-config file, and registers plugin file to hook lists
     *
     * @param plugin
     */
    private void RegisterPlugin(String plugin) throws IOException { //Yes, i know its impossible for there to not be a file passed in. Its java's fault this is here.
        String PluginFileRaw = "";
        //  try (Scanner scanner = new Scanner(new File("./plugins/" + plugin), String.valueOf(StandardCharsets.UTF_8))) {
        //  PluginFileRaw = scanner.useDelimiter("\\A").next();
        //}
        File fileRef = new File("./plugins/" + plugin);
        PluginFileRaw = new String(Files.readAllBytes(Paths.get(fileRef.getAbsolutePath())));
        //Add to hook lists if version is correct
        if (PluginFileRaw.contains("CLOTH_STITCH_VERSION:" + STITCH_HOOK_VERSION)) {
            if (PluginFileRaw.contains("OnServerInit")) {

                OnServerInitHook.add(plugin); //Store a ref to the lua file so we can exec it later
            }
            if (PluginFileRaw.contains("OnBlockCreateBy")) {
                OnBlockCreateByHook.add(plugin);
            }
            if (PluginFileRaw.contains("OnServerTick")) {
                OnServerTickHook.add(plugin);
            }
        } else {
            Logger logger = Logger.getLogger("Minecraft");
            logger.info("[Stitch] Plugin file " + plugin + " was not loaded: Plugin is marked for a different version of stitch");
        }

    }

    public void CallHook(String HookName, List<? extends Object> Args) {
        String output = null;
        Logger logger = Logger.getLogger("Minecraft");

        switch (HookName) {
            case "OnServerInit":
                for (int i = 0; i < OnServerInitHook.size(); i++)
                    output = CallFunctionFromLuaFile(OnServerInitHook.get(i), "OnServerInit", Args); //No usage of the args list as this takes no params
                //  logger.info("Called Hook: ServerInit With result " + output);
                break;
            case "OnServerTick":
                for (int i = 0; i < OnServerTickHook.size(); i++)
                    output = CallFunctionFromLuaFile(OnServerTickHook.get(i), "OnServerTick", Args); //No usage of the args list as this takes no params
                //logger.info("Called Hook: ServerTick With result " + output); - would be really spammy
            case "OnBlockCreateBy":
                for (int i = 0; i < OnBlockCreateByHook.size(); i++)
                    output = CallFunctionFromLuaFile(OnBlockCreateByHook.get(i), "OnBlockCreateBy", Args);
                //    logger.info("Called Hook: OnBlockCreateBy With result " + output);
                break;

        }
    }

    public String CallFunctionFromLuaFile(String filePath, String funcName, List<? extends Object> Args) {
        //run the lua script defining your function
        LuaValue _G = stichGlobals;
        _G.get("dofile").call(LuaValue.valueOf("./plugins/" + filePath));

        //call the function
        LuaValue Function = _G.get(funcName);
        LuaValue Return = null;
        String ArgType = HookToArgMappings.GetArgsListForHook(funcName);
        //Commmon LuaVals
        LuaValue Block;
        LuaValue Entity;
        LuaValue ArgsList = CoerceJavaToLua.coerce(Args);
        switch (ArgType) {
            case "None":
                Return = Function.call();
                break;
            case "Block,Entity":
                //Assume order of args list is the same as the param order
                Block = CoerceJavaToLua.coerce(Args.get(0));
                Entity = CoerceJavaToLua.coerce(Args.get(1));
                Return = Function.call(Block, Entity);
            case "Block,Entity,WasExplosion":
                Block = CoerceJavaToLua.coerce(Args.get(0));
                Entity = CoerceJavaToLua.coerce(Args.get(1));
                LuaValue WasExplosion = CoerceJavaToLua.coerce(Args.get(2));
                Return = Function.call(Block, Entity, WasExplosion);
            case "Entity,X,Y,Z":
                //Higher than 3 vals so we directly pass the full list. Whop-De-Do
                Return = Function.call(ArgsList);
        }


        //print out the result from the lua function
        return Return.tojstring(1);
    }


}
