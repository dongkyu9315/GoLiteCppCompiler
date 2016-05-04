all: clean
	$(PEEPDIR)/joosc *.java
opt: clean
	cd $(PEEPDIR)/JOOSA-src && $(MAKE)
	$(PEEPDIR)/joosc -O *.java 

clean:
	rm -f *.j *.class