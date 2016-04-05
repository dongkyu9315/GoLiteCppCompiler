package main

var a []int

func main() {
	var len = 200000
	for j:=len;j>=1;j--{
		a=append(a,j)
	}
	insertionSort(a,len)
	println("Sorted. Printing the first 100 sorted numbers")
	for i:=0;i<100;i++{
		println(a[i])
	}
	
}


func insertionSort(arr []int, size int) {

for j := 1; j < size; j++ {
  key := arr[j]
  i := j - 1
  for i >= 0 && arr[i] > key {
   arr[i+1] = arr[i]
   i = i - 1
  }

  arr[i+1] = key
 }
}