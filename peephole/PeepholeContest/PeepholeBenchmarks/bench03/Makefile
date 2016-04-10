all: clean
	$(PEEPDIR)/joosc *.java *.joos
opt: clean
	cd $(PEEPDIR)/JOOSA-src && $(MAKE)
	$(PEEPDIR)/joosc -O *.java *.joos

clean:
	rm -f *.j *.class