function result = toTimeDomain()
warning off all
host = 'C:\Users\User\Desktop\Research\Nexus_1\Nexus_1_liang_zero\';
final = '.wav';
index = 1;
k =16;
num = 20;
train = 50;
sample = 50000;

for x = 1:train
    [y,Fs,bits] = wavread(strcat(host,int2str(x),final));
    book{x} = y;
    sample = min(size(y,1),sample);
end 

sum = zeros(1,sample);

for x = 1:train
    for mm = 1: sample
       sum(mm) = sum(mm) + book{x}(mm);
    end 
end 


xxxx = 1: sample;
plot(xxxx, sum,'LineWidth',2);
title('Average of Original Data');
xlabel('Time');
ylabel('Amplitude')

 
 
     rrr = [];
    size(sum,2);
    for i=2: size(sum,2)-1
        temp=(sum(i-1)+sum(i)+sum(i+1))/3;
        if sum(i)>temp
            rrr=[rrr 1];
        else
            rrr=[rrr 0];
        end
    end

    
    paa = 1:500;
    prr = rrr(1:500);
    plot(paa,prr,'LineWidth',2);
    axis([0 318 -0.1 1.1]);
    title('Average of Original Data');
xlabel('Time');
ylabel('Amplitude')


bufferbuffer = [];
    buffer =0;
    for i = 1:5112
        if mod((i-1),8)==0
            temp= bitshift(rrr(i),7);
            buffer = bitor(buffer,temp);

        end
         if mod((i-1),8)==1
              temp=bitshift(rrr(i),6);  
              buffer = bitor(buffer,temp);

         end
         if mod((i-1),8)==2
              temp=bitshift(rrr(i),5);  
              buffer = bitor(buffer,temp);

         end
         if mod((i-1),8)==3
              temp=bitshift(rrr(i),4);  
              buffer = bitor(buffer,temp);
         end
         
         if mod((i-1),8)==4
              temp=bitshift(rrr(i),3);  
              buffer = bitor(buffer,temp);
         end

         if mod((i-1),8)==5
              temp=bitshift(rrr(i),2);  
              buffer = bitor(buffer,temp);
         end
         
         if mod((i-1),8)==6
              temp=bitshift(rrr(i),1);  
              buffer = bitor(buffer,temp);
         end
         
         if mod((i-1),8)==7
              temp=bitshift(rrr(i),0);  
              buffer = bitor(buffer,temp);
              bufferbuffer = [bufferbuffer buffer];
              buffer=0;
         end
    end
    
    
     size(bufferbuffer)
    buffer1=bufferbuffer(1:100);
    buffer2 = bufferbuffer(105:204);
    buffer3 = bufferbuffer(209:308);
    %buffer4 = bufferbuffer(313:412);
    ff = [];
    
    for i = 1: size(buffer1,2)
        AA1 = buffer1(i);
        BB1 = buffer2(i);
       % CC1 = buffer3(i);
        %DD1 = buffer4(i);
        temp = bitand(AA1,BB1);
        %temp2(i) = bitxor(temp(i),i);
        %temp3 = bitand(temp2,DD1);
        ff = [ff temp];
    end
  x2= 1: size(ff,2);
  plot(x2,ff,'LineWidth',2)
  title('Encoding Result')
  
  
  
    fip=fopen('binary3.bin','wb');
    fwrite(fip,ff,'int8');
    sta=fclose(fip);

%%
function m = melfb(p, n, fs)

f0 = 700 / fs;
fn2 = floor(n/2);

lr = log(1 + 0.5/f0) / (p+1);

% convert to fft bin numbers with 0 for DC term
bl = n * (f0 * (exp([0 1 p p+1] * lr) - 1));

b1 = floor(bl(1)) + 1;
b2 = ceil(bl(2));
b3 = floor(bl(3));
b4 = min(fn2, ceil(bl(4))) - 1;

pf = log(1 + (b1:b4)/n/f0) / lr;
fp = floor(pf);
pm = pf - fp;

r = [fp(b2:b4) 1+fp(1:b3)];
c = [b2:b4 1:b3] + 1;
v = 2 * [1-pm(b2:b4) pm(1:b3)];

m = sparse(r, c, v, p, 1+fn2);

%%
function M3 = blockFrames(s, fs, m, n)

l = length(s);
nbFrame = floor((l - n) / m) + 1;

for i = 1:n
    for j = 1:nbFrame
        M(i, j) = s(((j - 1) * m) + i);
    end
end

h = hamming(n);
M2 = diag(h) * M;

for i = 1:nbFrame
    M3(:, i) = fft(M2(:, i));
end

% x = 1:n
% plot(x,M3(:,1));

%%
function d = disteu(x, y)

[M, N] = size(x);
[M2, P] = size(y);

if (M ~= M2)
    error('Matrix dimensions do not match.')
end

d = zeros(N, P);


for ii=1:N
    for jj=1:P
        d(ii,jj) = mydistance(x(:,ii),y(:,jj),2);
    end
end

%%
function r = mfcc(s, fs)

m = 100;
n = 256;
l = length(s);

nbFrame = floor((l - n) / m) + 1;

for i = 1:n
    for j = 1:nbFrame
        M(i, j) = s(((j - 1) * m) + i);
    end
end

h = hamming(n);

M2 = diag(h) * M;
    x = 1/128*500:1/128*500:n/2/128*500
% y = 1:n;
for i = 1:nbFrame
    frame(:,i) = fft(M2(:, i));
end
% temp = frame(:,10);
% half = temp(1:n/2);
% plot(y,M(:,10));



t = n / 2;
tmax = l / fs;

m = melfb(20, n, fs);
n2 = 1 + floor(n / 2);
z = m * abs(frame(1:n2, :)).^2;
x = 1:20


r = dct(log(z));
for aa = 1: 20
plot(x,r(:,aa))

end

%%
function [out] = mydistance(x,y,tipo)

if tipo == 0
    out = sum((x-y).^2).^0.5;
end
if tipo == 1
    out = sum(abs(x-y));
end

if tipo == 2
    pesi = zeros(size(x));
    pesi(1)     = 0.20;
    pesi(2)     = 0.90;
    pesi(3)     = 0.95;
    pesi(4)     = 0.90;
    pesi(5)     = 0.70;
    pesi(6)     = 0.90;
    pesi(7)     = 1.00;
    pesi(8)     = 1.00;
    pesi(9)     = 1.00;
    pesi(10)    = 0.95;
    pesi(11:13) = 0.30;
    
    out = sum(abs(x-y).*pesi);
end

%%
function r = vqlbg(d,k)
e   = .01;
r   = mean(d, 2);
dpr = 10000;

for i = 1:log2(k)
    r = [r*(1+e), r*(1-e)];
    
    while (1 == 1)
        z = disteu(d, r);
        [m,ind] = min(z, [], 2);
        t = 0;
        for j = 1:2^i
            r(:, j) = mean(d(:, find(ind == j)), 2);
            x = disteu(d(:, find(ind == j)), r(:, j));
            for q = 1:length(x)
                t = t + x(q);
            end
        end
        if (((dpr - t)/t) < e)
            break;
        else
            dpr = t;
        end
    end
end
