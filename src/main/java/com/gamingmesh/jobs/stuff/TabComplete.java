package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.StringUtil;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobLimitedItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Quest;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
	List<String> completionList = new ArrayList<>();

	if (args.length == 1) {
	    String PartOfCommand = args[0];
	    List<String> temp = new ArrayList<>();

	    for (String BCmd : Jobs.getCommandManager().GetCommands(sender).keySet()) {
		temp.add(BCmd);
	    }
	    StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
	}
	if (args.length > 1)
	    for (int i = 1; i <= args.length; i++)
		if (args.length == i + 1) {

		    String PartOfCommand = args[i];

		    if (!Jobs.getGCManager().getCommandArgs().containsKey(args[0].toLowerCase()))
			break;

		    List<String> ArgsList = Jobs.getGCManager().getCommandArgs().get(args[0].toLowerCase());

		    if (ArgsList.size() < i)
			continue;

		    String arg = ArgsList.get(i - 1);
		    List<String> t2 = new ArrayList<>();

		    if (arg.contains("%%"))
			for (String one : arg.split("%%")) {
			    t2.add(one);
			}
		    else
			t2.add(arg);

		    List<String> temp = new ArrayList<>();
		    for (String ar : t2) {
			switch (ar) {
			case "[time]":
			    temp.add("1hour10minute20s");
			    break;
			case "[questname]":
			case "[quest]":
			    Job job = Jobs.getJob(args[i - 1]);
			    if (job != null) {
				for (Quest q : job.getQuests()) {
				    temp.add(q.getQuestName());
				}
			    }
			    break;
			case "[jobname]":
			case "[newjob]":
			    for (Job one : Jobs.getJobs()) {
				temp.add(one.getName());
			    }
			    break;
			case "[playername]":
			    pl: for (Player player : Bukkit.getOnlinePlayers()) {
				// ignore hidden players
				if (Jobs.getGCManager().FilterHiddenPlayerFromTabComplete && player.hasMetadata("vanished")) {
				    // TODO add essentials & cmi support
				    for (MetadataValue meta : player.getMetadata("vanished")) {
					if (meta.asBoolean()) {
					    continue pl;
					}
				    }
				}

				temp.add(player.getName());
			    }
			    break;
			case "[action]":
			    for (ActionType action : ActionType.values()) {
				temp.add(action.getName());
			    }
			    break;
			case "[jobitemname]":
			    for (JobItems one : ItemBoostManager.getItems().values()) {
				temp.add(one.getNode());
			    }
			    if (args.length > 3 && args[3].equalsIgnoreCase("limiteditems")) {
				Job oneJob = Jobs.getJob(args[i - 1]);
				if (oneJob != null)
				    for (JobLimitedItems limitedItem : oneJob.getLimitedItems().values()) {
					temp.add(limitedItem.getNode());
				    }
			    }
			    break;
			case "[boosteditems]":
			    for (JobItems one : ItemBoostManager.getItems().values()) {
				temp.add(one.getNode());
			    }
			    break;
			case "[oldjob]":
			    JobsPlayer onePlayerJob = Jobs.getPlayerManager().getJobsPlayer(args[i - 1]);
			    if (onePlayerJob != null)
				for (JobProgression oneOldJob : onePlayerJob.getJobProgression()) {
				    temp.add(oneOldJob.getJob().getName());
				}
			    break;
			case "[oldplayerjob]":
			    if (sender instanceof Player) {
				onePlayerJob = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
				if (onePlayerJob != null)
				    for (JobProgression oneOldJob : onePlayerJob.getJobProgression()) {
					temp.add(oneOldJob.getJob().getName());
				    }
			    }
			    break;
			default:
			    temp.add(ar);
			    break;
			}
		    }

		    StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
		}

	Collections.sort(completionList);
	return completionList;
    }
}
