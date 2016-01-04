warning off;
inputfolder = '/Users/Guo/Documents/MATLAB/Continuous Mode Voice Recognition/data/Nexus_2/Nexus_2_liang_dog/';
Fs = 11025;
data = readin(inputfolder);
num_of_segment = size(data,2);

%add chain list
for i = 1 : num_of_segment
x = getMFCC(data{i},Fs);
y = ToStatechain(x);
result{i} = y;
end
TRANS = getTRANS(result);
EMIS = getEMIS(result);



table = zeros(11*13);
count =0;
target = '/Users/Guo/Documents/MATLAB/Continuous Mode Voice Recognition/data/Nexus_2/Nexus_2_gao_zero/';
accurate = 0;
testfolder = readin(target);
num_of_target = size(testfolder,2);
for i = 1: num_of_target
   x = getMFCC(testfolder{i},Fs);
   y = ToStatechain(x);
   length = size(y,2); 
   percent = 0;
   for j = 1: length-1
       s = y(j);
       e = y(j+1);
       if(e~=-1 && s~=-1)
       if(TRANS(s,e)==0)
       percent = percent -length;
       else
       percent = percent +TRANS(s,e);
       end
       end
   end
   percent = percent/length;
   p = 0;
   p = p+percent;
  
   %disp(percent)
   if(percent>0.3601)
      accurate=accurate+1;
   end
end
disp(accurate/100)
disp(p*0.9);









