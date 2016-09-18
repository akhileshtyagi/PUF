To gather data:
1. Use Android keyboard app to gather [time,x,y,pressure] data
2. Use Ruby script convert_xy.rb to convert the x,y values into keycodes
2.1 the reason for this is that x,y values don't always equate to the same keycode
3. The data file produced by convert_xy.rb is the file that can be used by Chain for analysis
