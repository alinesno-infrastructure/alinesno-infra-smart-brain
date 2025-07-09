import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

export const htmlToPDF = (title, html) => {
    html2canvas(html, {
        allowTaint: false,
        useCORS: true,
        dpi: window.devicePixelRatio * 2,
        scale: 2
    }).then(canvas => {
        const pdf = new jsPDF('p', 'mm', 'a4');
        const imgWidth = 190;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;
        const pageHeight = 295;
        let heightLeft = imgHeight;
        let position = 0;

        pdf.addImage(canvas.toDataURL('image/jpeg', 1.0), 'JPEG', 10, 10, imgWidth, imgHeight);
        heightLeft -= pageHeight;

        while (heightLeft >= 0) {
            position = heightLeft - imgHeight;
            pdf.addPage();
            pdf.addImage(canvas.toDataURL('image/jpeg', 1.0), 'JPEG', 10, position, imgWidth, imgHeight);
            heightLeft -= pageHeight;
        }

        pdf.save(`${title}.pdf`);
    });
};