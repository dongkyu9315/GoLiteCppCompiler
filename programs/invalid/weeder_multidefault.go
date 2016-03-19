package main

var x int
var y int
func main(){
	switch x := 4; {  // missing switch expression means "true"
	case x < 0: return -x
	default: return x
	case x >3 : return x+1
	default : return x+2
	}
}