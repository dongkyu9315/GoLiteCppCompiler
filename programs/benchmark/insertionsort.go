package main

import "fmt"
var a []int

func main() {
	
	for j:=200000;j>=1;j--{
		a=append(a,j)
	}
	insertionSort(a)
	fmt.Println("Sorted. Printing the first 100 sorted numbers")
	fmt.Println(a[:100])
	
}


func insertionSort(arr []int) {

for j := 1; j < len(arr); j++ {
  key := arr[j]
  i := j - 1
  for i >= 0 && arr[i] > key {
   arr[i+1] = arr[i]
   i = i - 1
  }

  arr[i+1] = key
 }
}