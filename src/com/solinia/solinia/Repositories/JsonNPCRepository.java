package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Factories.ISoliniaAARankTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaNPC;
import com.solinia.solinia.Models.SoliniaAARank;
import com.solinia.solinia.Models.SoliniaNPC;

public class JsonNPCRepository implements IRepository<ISoliniaNPC> {
	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaNPC> NPCs = new ConcurrentHashMap<Integer, ISoliniaNPC>();

	@Override
	public void add(ISoliniaNPC item) {
		this.NPCs.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaNPC> items) {
		for(ISoliniaNPC i : items)
		{
			this.NPCs.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaNPC item) {
		this.NPCs.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaNPC item) {
		this.NPCs.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaNPC> filter) {
		for(ISoliniaNPC i : NPCs.values().stream().filter(filter).collect(Collectors.toList()))
		{
			NPCs.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaNPC> query(Predicate<ISoliniaNPC> filter) {
		return NPCs.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaNPC> file = new ArrayList<ISoliniaNPC>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaNPC>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		NPCs.clear();
		for(ISoliniaNPC i : file)
		{
			NPCs.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + NPCs.size() + " NPCs");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(NPCs.values(), new TypeToken<List<SoliniaNPC>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + NPCs.size() + " NPCs");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}
}
