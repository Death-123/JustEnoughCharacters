package me.towdium.jecharacters;

import com.google.gson.GsonBuilder;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecharacters.util.Profiler;
import me.towdium.jecharacters.util.StringMatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

/**
 * Author: Towdium
 * Date:   14/06/17
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JechCommand extends CommandBase {
    /*
    private static List<String> parseArg(String[] args) {
        boolean quote = false;
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> buffer = new ArrayList<>();
        for (String s : args) {
            if (!quote && s.startsWith("\"")) {
                if (s.endsWith("\"")) {
                    ret.add(s.substring(1, s.length() - 1));
                } else {
                    quote = true;
                    buffer.add(s.substring(1));
                }
                continue;
            } else if (quote && s.endsWith(("\""))) {
                quote = false;
                buffer.add(s.substring(0, s.length() - 1));
                ret.add(String.join(" ", buffer));
                continue;
            } else if (s.contains("\"")) {
                throw new RuntimeException("Illegal format.");
            }

            if (quote) {
                buffer.add(s);
            } else {
                ret.add(s);
            }
        }
        if (quote) {
            throw new RuntimeException("Illegal format.");
        } else {
            return ret;
        }
    }
    */

    @Override
    public String getName() {
        return "jech";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.desc";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equals("profile")) {
            Thread t = new Thread(() -> {
                Profiler.Report r = Profiler.run();
                try (FileOutputStream fos = new FileOutputStream("logs/jecharacters-profiler.txt")) {
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(new GsonBuilder().setPrettyPrinting().create().toJson(r));
                    osw.flush();
                    sender.sendMessage(new TextComponentString(I18n.format("chat.saved")));
                } catch (IOException e) {
                    sender.sendMessage(new TextComponentString(I18n.format("chat.saveError")));
                }
            });
            t.setPriority(Thread.MIN_PRIORITY);
            t.run();
        } else if (args.length == 2 && args[0].equals("verbose")) {
            if (args[1].toLowerCase().equals("true"))
                StringMatcher.verbose = true;
            else if (args[1].toLowerCase().equals("true"))
                StringMatcher.verbose = false;
            else sender.sendMessage(new TextComponentTranslation("command.unknown"));
        } else {
            sender.sendMessage(new TextComponentTranslation("command.unknown"));
        }
    }

    @Override
    public List<String> getTabCompletions(
            MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, "profile", "verbose");
        else if (args.length == 2 && args[0].equals("verbose"))
            return getListOfStringsMatchingLastWord(args, "true", "false");
        else
            return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
