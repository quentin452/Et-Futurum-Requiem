package ganymedes01.etfuturum.core.utils;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
/**
 * Adapted from Jabelar's tutorials
 * Taken from VillageNames with permission
 * http://jabelarminecraft.blogspot.com/p/minecraft-forge-1721710-making-mod.html
 * Parallel threading provided by Roadhog360
 * @author AstroTibs
 */

public class VersionChecker extends Thread
{
	public static final VersionChecker instance = new VersionChecker();
	
	private static boolean isLatestVersion = false;
	private static boolean warnaboutfailure = false;
    private static String latestVersion = "";
    private static boolean isUpdateCheckFinished = false;
    private static boolean quitChecking = false;
    private static boolean hasThreadStarted = false;
    private static List<String> downloadURLs = new LinkedList<String>();
    
	@Override
	public void run()
	{
        InputStream in = null;
        
        try
        {
        	URL url = new URL(Reference.VERSION_URL);
            in = url.openStream();
        } 
        catch (Exception e)
        {
        	if (!warnaboutfailure)
        	{
            	Logger.error("Could not connect with server to compare " + Reference.MOD_NAME + " version");
        		Logger.error("Check for new versions at the following websites:");
        		Logger.error(Reference.MODRINTH_URL);
        		Logger.error(Reference.CURSEFORGE_URL);
        		Logger.error(Reference.GITHUB_URL);
            	warnaboutfailure=true;
        	}
        }
        
        try
        {
        	List<String> lines = IOUtils.readLines(in, Charset.defaultCharset());
            latestVersion = lines.get(0);
            for(int i = 1; i < lines.size(); i++) {
            	if(!lines.get(i).startsWith("#") && lines.get(i).contains("|")) {
                	downloadURLs.add(lines.get(i));
            	}
            }
        }
        catch (Exception e)
        {
        	if (!warnaboutfailure)
        	{
        		Logger.error("Failed to compare " + Reference.MOD_NAME + " version");
        		Logger.error(Reference.MODRINTH_URL);
        		Logger.error(Reference.CURSEFORGE_URL);
        		Logger.error(Reference.GITHUB_URL);
        		warnaboutfailure=true;
        	}
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        
        isLatestVersion = Reference.VERSION_NUMBER.equals(latestVersion);
        
        if (!this.isLatestVersion() && !latestVersion.equals("") && !latestVersion.equals(null))
        {
        	Logger.info("This version of "+Reference.MOD_NAME+" (" + Reference.VERSION_NUMBER + ") differs from the latest version: " + latestVersion);
        }
        
        isUpdateCheckFinished = true;
    }
	
    public boolean isLatestVersion()
    {
    	return isLatestVersion;
    }
    
    public String getLatestVersion()
    {
    	return latestVersion;
    }
	
    /**
     * PlayerTickEvent is going to be used for version checking.
     * @param event
     */
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onPlayerTickEvent(PlayerTickEvent event)
    {
    	// Used to repeat the version check
    	if (
    			(latestVersion.equals(null) || latestVersion.equals(""))
    			&& !warnaboutfailure // Skip the "run" if a failure was detected
    			&& !hasThreadStarted
    			)
    	{
    		start();
    		hasThreadStarted=true;
    	}
    	
    	if (
    			event.player.ticksExisted>=200
    			&& !quitChecking
    			&& isUpdateCheckFinished) 
    	{
    		Logger.error(Reference.MOD_NAME + " version check failed.");
    		Logger.error("Check for new versions at the following websites:");
    		Logger.error(Reference.MODRINTH_URL);
    		Logger.error(Reference.CURSEFORGE_URL);
    		Logger.error(Reference.GITHUB_URL);
    		quitChecking=true;
    	}
    	
        if (
        		event.player.worldObj.isRemote
        		&& event.phase == Phase.END // Stops doubling the checks unnecessarily
        		&& event.player.ticksExisted>=40
        		&& isUpdateCheckFinished
        		&& !quitChecking
        		)
        {
        	// Ordinary version checker
        	if (
            		!latestVersion.equals(null)
            		&& !latestVersion.equals("")
        			)
        	{
                quitChecking=true;
                
                if(!instance.isLatestVersion()) {
	
	                String text = String.format(StatCollector.translateToLocal("gui.chat.update"), 
	                		EnumChatFormatting.AQUA.toString()+EnumChatFormatting.ITALIC.toString()+Reference.MOD_NAME+EnumChatFormatting.RESET.toString(), 
	                		EnumChatFormatting.YELLOW.toString()+latestVersion+EnumChatFormatting.RESET.toString());
	                event.player.addChatComponentMessage(new ChatComponentText(text));
	                
	                ChatComponentText updateLinks = new ChatComponentText("");
	                
	                for(int i = 0; i < downloadURLs.size(); i++) {
	                	String url = downloadURLs.get(i);
	                	Logger.debug(event);
	                	ChatComponentText urlComponent = new ChatComponentText("[" + url.split("\\|")[0] + "]");
	                	urlComponent.getChatStyle().setColor(EnumChatFormatting.getValueByName(url.split("\\|")[1]));
	                	urlComponent.getChatStyle().setBold(true);
	                	urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, url.split("\\|")[2]));
	                	
	                	ChatComponentText hoverComponent = new ChatComponentText(String.format(StatCollector.translateToLocal("gui.chat.update.download"), url.split("\\|")[0]));
	                	hoverComponent.getChatStyle().setColor(urlComponent.getChatStyle().getColor());
	                	urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));
	                	
	                	updateLinks.appendSibling(urlComponent);
	                	if(downloadURLs.size() > i) { //Don't add a space if it's the last URL
	                    	updateLinks.appendText(" ");
	                	}
	                }
					event.player.addChatComponentMessage(updateLinks);
                }
        	}
        }
        
        if (quitChecking)
        {
    		FMLCommonHandler.instance().bus().unregister(instance);
    		return;
        }
    }
}
