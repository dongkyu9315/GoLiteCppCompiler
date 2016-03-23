package main

var x int
var y int
func main() int{
	switch x := 4; 3.14{  // missing switch expression means "true"
	case 3.14 : return -x
	default: return x
	case 3 : return x+1
	}
	return 2
}