ROOMACTIONS = RoomAction.java BFRoomAction.java FeastRoomAction.java CoinRoomAction.java EmptyRoomAction.java GrueRoomAction.java TreasureRoomAction.java FactorialRoomAction.java HuntRoomAction.java ExitRoomAction.java JokeRoomAction.java

SRC = $(ROOMACTIONS) Room.java GrueHunt.java InGenerate.java

GrueHunt: $(SRC)
	javac $(SRC)

opt: $(SRC)
	cd $(PEEPDIR)/JOOSA-src && $(MAKE)
	$(PEEPDIR)/joosc -O $(SRC)

joos: $(SRC)
	$(PEEPDIR)/joosc $(SRC)

gen:
	javac InGenerate.java
	java InGenerate > in1

clean:
	rm *.class *.j

