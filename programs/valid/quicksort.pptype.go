package test 

 func partition ( arr [] int , left , right int ) int { //: ([] int, int, int) : int 
 	 i , j := left , right 
 	 var tmp int //: int 
 	 pivot := arr [ ( left + right ) / 2 ] 
 	 for ; i <= j ; {
 	 	 for ; arr [ i ] < pivot ; {
 	 	 	 i ++ 
 	 	 }
 
 	 	 
 	 	 for ; arr [ j ] > pivot ; {
 	 	 	 j -- 
 	 	 }
 
 	 	 
 	 	 if i <= j {
 	 	 	 tmp = arr [ i ] 
 	 	 	 arr [ i ] = arr [ j ] 
 	 	 	 arr [ j ] = tmp 
 	 	 	 i ++ 
 	 	 	 j -- 
 	 	 }
 
 	 	 
 	 }
 
 	 
 	 return i 
 }
 func quickSort ( arr [] int , left , right int ) { //: ([] int, int, int) : void 
 	 index := partition ( arr , left , right ) 
 	 if left < index - 1 {
 	 	 quickSort ( arr , left , index - 1 ) 
 	 }
 
 	 
 	 if index < right {
 	 	 quickSort ( arr , index , right ) 
 	 }
 
 	 
 }
 