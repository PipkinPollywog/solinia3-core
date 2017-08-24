package com.solinia.solinia.Interfaces;

import java.util.List;

public interface IConfigurationManager {

	List<ISoliniaRace> getRaces();

	ISoliniaRace getRace(int raceId);

	ISoliniaRace getRace(String race);

	void commit();

	void addRace(ISoliniaRace race);

	int getNextRaceId();

	List<ISoliniaClass> getClasses();

	ISoliniaClass getClassObj(int classId);

	ISoliniaClass getClassObj(String classname);

	void addClass(ISoliniaClass classobj);

	int getNextClassId();

	boolean isValidRaceClass(int raceId, int classId);

	void AddRaceClass(int raceId, int classId);
}
