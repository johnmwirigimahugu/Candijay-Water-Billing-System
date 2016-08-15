$(document).ready(function(){
    $('#master-checkbox').click(function(e) {
        var chk = $(this).prop('checked');
        $('input', oTable_invoice.$('tr', {"filter": "applied"} )).prop('checked',chk);
    });
    $('#apply').on('click', function(e){
        e.preventDefault();
        var form = $('#form');
        $.post(form.attr('action')+'/print-check', form.serialize(), function(response){
            if(response.status === "SUCCESS")
                openReport('POST', form.attr('action')+'/print', response.result, '_blank');
        })
    });
});