all: paper

paper: paper.tex
	pdflatex paper.tex;
	bibtex paper.aux;
	pdflatex paper.tex;
	pdflatex paper.tex;

clean:
	rm *.log
	rm *.aux
	rm *.bbl