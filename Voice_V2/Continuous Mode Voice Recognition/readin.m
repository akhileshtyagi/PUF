function r=readin(infolder)
warning off;
D = dir(infolder);
filenumber = length(D);
for i = 4 : filenumber
   [y,Fs,bits] = wavread(strcat(infolder,D(i).name));
    r{i-3} = y;
end
end